import unittest
import requests
import argparse
import json
import sys
import logging
import yaml
from datetime import date, datetime


class integration_tests(unittest.TestCase):

    # Set class variables for tests
    @classmethod
    def setup(cls, config_file_path, open_api_path):
        with open(open_api_path, 'r') as yaml_stream:
            cls.api_spec = yaml.load(yaml_stream)

        with open(config_file_path, 'r') as json_stream:
            config_file_json = json.load(json_stream)

        cls.host = config_file_json["hostname"]
        cls.base_path = cls.api_spec["basePath"]
        cls.osu_id = config_file_json["osu_id"]
        cls.base_url = f"{cls.host}{cls.base_path}/students"
        cls.url_with_id = f"{cls.base_url}/{cls.osu_id}"

        cls.class_schedule_term = config_file_json["class_schedule_term"]

        client_id = config_file_json["client_id"]
        client_secret = config_file_json["client_secret"]

        access_token = cls.get_access_token(config_file_json["token_api"],
                                            client_id, client_secret)

        # Set headers and query parameters
        cls.auth_header = {'Authorization': 'Bearer {}'.format(access_token)}

    # Helper method to get an access token
    @staticmethod
    def get_access_token(url, client_id, client_secret):
        post_data = {
            'client_id': client_id,
            'client_secret': client_secret,
            'grant_type': 'client_credentials'
        }

        request = requests.post(url, data=post_data)
        response = request.json()

        return response["access_token"]

    # Helper method to make a web request and lightly validate the response.
    def __make_request(self,
                       endpoint,
                       response_time,
                       status_code=200,
                       params=None):
        if status_code == 404:
            # If a 404 is expected, send a bad ID.
            url = f"{self.base_url}/93badID/{endpoint}"
        else:
            url = f"{self.url_with_id}/{endpoint}"

        request = requests.get(url, params=params, headers=self.auth_header)

        self.assert_response_time(request, response_time)
        self.assertEqual(request.status_code, status_code)

        if status_code == 200:
            # Only test self links for successful responses.
            self.assertEqual(request.url, request.json()["links"]["self"])

        return request

    # Tests a date matches a desired format by attempting to parse it.
    def assert_date_format(self, date, date_format):
        logging.debug(f"Raw date string: {date}.")
        try:
            datetime.strptime(date, date_format)
        except ValueError:
            self.fail(f"{date} does not match the format {date_format}")

    # Tests a API call is within the acceptable response time.
    def assert_response_time(self, request, max_elapsed_seconds):
        elapsed_seconds = request.elapsed.total_seconds()
        logging.debug("Request took {} second(s)".format(elapsed_seconds))
        self.assertLess(elapsed_seconds, max_elapsed_seconds)

    # Tests an error response and error message
    def assert_error_response(self, request, message):
        response = request.json()
        errors = response["errors"]
        self.assertEqual(len(errors), 1)

        properties = self.__get_properties("Error")
        self.assert_object_matches_spec(properties, response)

        error = errors[0]

        self.assert_object_matches_spec(
            properties["errors"]["items"]["properties"], error)
        self.assertEqual(message, error["detail"])

    # Main method for comparing an API response to the OpenAPI specification.
    # Gets the expected value of the field, and compares the actual result.
    def assert_object_matches_spec(self, properties, actual):
        self.assertEqual(len(properties), len(actual))

        for field, field_properties in properties.items():
            logging.debug(f"Testing field: {field}")
            self.assertIn(field, actual)

            expected_type = self.__openapi_type(field_properties)
            logging.debug(f"Expected type: {expected_type}")

            # If the field in the response is null and is not a dict,
            # skip the rest the iteration.
            if actual[field] is None and expected_type != dict:
                continue

            self.assertIsInstance(actual[field], expected_type)

            if expected_type == str and "format" in field_properties:
                format = field_properties["format"]

                # Make sure dates as formatted correctly
                if format == "date-time":
                    self.assert_date_format(actual[field],
                                            "%Y-%m-%dT%H:%M:%SZ")
                elif format == "date":
                    self.assert_date_format(actual[field], "%Y-%m-%d")

            # Make sure the returned value is in the documented list of values
            if "enum" in field_properties:
                self.assertIn(actual[field], field_properties["enum"])

            # If the field is an object, evaluate the object contents
            if expected_type == dict:
                self.assert_object_matches_spec(field_properties["properties"],
                                                actual[field])

    # Mapping between OpenAPI data types and python data types
    @staticmethod
    def __openapi_type(properties):
        if "type" in properties:
            plain_type = properties["type"]
            logging.debug(f"OpenAPI type: {plain_type}")

            if plain_type == "string":
                return str
            elif plain_type == "integer":
                return int
            elif plain_type == "number":
                if "format" in properties:
                    type_format = properties["format"]
                    if type_format in ["float", "double"]:
                        return float
                    elif type_format in ["integer", "int32", "int64"]:
                        return int
                    else:
                        # Treat int as default
                        return int
                else:
                    # Treat int as default
                    return int
            elif plain_type == "boolean":
                return bool
            elif plain_type == "array":
                return list
            elif plain_type == "object":
                return dict
            else:
                logging.warn("Unrecognized OpenAPI data type.")
                return None
        elif "properties" in properties:
            # If a properties object exists but no type is given,
            # default to dict.
            return dict
        else:
            logging.warn("OpenAPI property contains no type or properties.")
            return None

    # Get the top level properties of an object in an OpenAPI spec.
    def __get_properties(self, object_title):
        return self.api_spec["definitions"][object_title]["properties"]

    # Get the properties of a result object where "data" is an object.
    def __get_properties_for_one_of_one(self, object_title):
        return self.__get_properties(
            object_title)["data"]["properties"]["attributes"]["properties"]

    # Get the properties of a result object where "data" is an array.
    def __get_properties_for_one_of_many(self, object_title):
        return self.__get_properties(object_title)["data"]["items"][
            "properties"]["attributes"]["properties"]

    def test_account_balance(self):
        request = self.__make_request("account-balance", 4)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties_for_one_of_one(
            "AccountBalanceResultObject")

        self.assert_object_matches_spec(properties, attributes)

    def test_account_transactions(self):
        request = self.__make_request("account-transactions", 4)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties_for_one_of_one(
            "AccountTransactionsResultObject")

        self.assert_object_matches_spec(properties, attributes)

        for transaction in attributes["transactions"]:
            self.assert_object_matches_spec(
                properties["transactions"]["items"]["properties"], transaction)

    def test_gpa(self):
        request = self.__make_request("gpa", 4)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties_for_one_of_one(
            "GradePointAverageResultObject")

        self.assert_object_matches_spec(properties, attributes)

        for level in attributes["gpaLevels"]:
            self.assert_object_matches_spec(
                self.__get_properties("GradePointAverageObject"), level)

    def test_academic_status(self):
        request = self.__make_request("academic-status", 4)
        resource_objects = request.json()["data"]

        properties = self.__get_properties_for_one_of_many(
            "AcademicStatusResultObject")

        for resource_object in resource_objects:
            attributes = resource_object["attributes"]
            term = attributes["term"]
            self.assertEqual(f"{self.osu_id}-{term}", resource_object["id"])
            self.assert_object_matches_spec(properties, attributes)

            for gpa_level in attributes["gpa"]:
                self.assert_object_matches_spec(
                    self.__get_properties("GradePointAverageObject"),
                    gpa_level)

    def test_classification(self):
        request = self.__make_request("classification", 6)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties_for_one_of_one(
            "ClassificationResultObject")

        self.assert_object_matches_spec(properties, attributes)

    def test_grades(self):
        request = self.__make_request("grades", 6)
        resource_objects = request.json()["data"]

        for resource_object in resource_objects:
            attributes = resource_object["attributes"]
            term = attributes["term"]
            crn = attributes["courseReferenceNumber"]
            logging.debug(f"testing grades for term {term}, crn {crn}")
            self.assertEqual(f"{self.osu_id}-{term}-{crn}",
                             resource_object["id"])

            properties = self.__get_properties("GradesResultObject")["data"][
                "items"]["properties"]["attributes"]["properties"]

            self.assert_object_matches_spec(properties, attributes)

    def test_class_schedule(self):
        request = self.__make_request("class-schedule", 5, 200,
                                      {"term": self.class_schedule_term})
        resource_objects = request.json()["data"]

        for resource_object in resource_objects:
            attributes = resource_object["attributes"]
            term = attributes["term"]
            crn = attributes["courseReferenceNumber"]
            logging.debug(f"testing class schedule for term {term}, crn {crn}")
            self.assertEqual(f"{self.osu_id}-{term}-{crn}",
                             resource_object["id"])

            properties = self.__get_properties("ClassScheduleResultObject")[
                "data"]["items"]["properties"]["attributes"]["properties"]

            self.assert_object_matches_spec(properties, attributes)

    def test_class_schedule_no_term(self):
        request = self.__make_request("class-schedule", 5, 400)
        self.assert_error_response(request,
                                   "Term (query parameter) is required.")

    def test_class_schedule_bad_term(self):
        request = self.__make_request("class-schedule", 3, 400,
                                      {"term": "badterm"})
        self.assert_error_response(request, "Term is invalid.")

    def test_holds(self):
        request = self.__make_request("holds", 3)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties_for_one_of_one("HoldsResultObject")

        self.assert_object_matches_spec(properties, attributes)

        for hold in attributes["holds"]:
            self.assert_object_matches_spec(
                properties["holds"]["items"]["properties"], hold)

    def test_work_study(self):
        request = self.__make_request("work-study", 1.5)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties_for_one_of_one(
            "WorkStudyResultObject")

        self.assert_object_matches_spec(properties, attributes)

        for award in attributes["awards"]:
            self.assert_object_matches_spec(
                properties["awards"]["items"]["properties"], hold)

    def test_dual_enrollment(self):
        request = self.__make_request("dual-enrollment", 1.5)
        resource_objects = request.json()["data"]

        properties = self.__get_properties_for_one_of_many(
            "DualEnrollmentResultObject")

        for resource_object in resource_objects:
            attributes = resource_object["attributes"]
            term = attributes["term"]
            logging.debug(f"testing term {term} for dual enrollment")
            self.assertEqual(f"{self.osu_id}-{term}", resource_object["id"])
            self.assert_object_matches_spec(properties, attributes)

    def test_not_found(self):
        endpoints = self.api_spec["paths"]

        for endpoint in endpoints:
            resource = endpoint.split("/")[-1]
            logging.debug(f"testing {resource} returns 404")
            request = self.__make_request(resource, 4, 404)
            self.assert_error_response(
                request,
                "The information requested was not found. " +
                "If this is incorrect, please contact application support."
            )

            # The same resource should work with a valid ID
            self.__make_request(resource, 5, 200)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Students API integration tests')
    parser.add_argument(
        '--config',
        '-i',
        dest='config_file',
        help='Path to configuration file containing API credentials',
        required=True)
    parser.add_argument(
        '--openapi',
        '-s',
        dest='open_api',
        help='Path to yaml formatted openAPI specification',
        required=True)
    parser.add_argument(
        '--debug',
        dest='debug',
        help='Enable debug logging mode',
        action='store_true')
    arguments, unittest_args = parser.parse_known_args()

    if arguments.debug:
        logging.basicConfig(level=logging.DEBUG)
    else:
        logging.basicConfig(level=logging.INFO)

    # Load configuration file
    config_file_path = arguments.config_file
    open_api_path = arguments.open_api
    integration_tests.setup(config_file_path, open_api_path)

    unittest.main(argv=sys.argv[:1] + unittest_args)
