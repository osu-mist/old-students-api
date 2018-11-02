import unittest
import requests
import argparse
import json
import sys
import logging
import yaml
from datetime import date, datetime
from urllib import parse
from random import randint


class integration_tests(unittest.TestCase):

    # Set class variables related to configurations for tests
    @classmethod
    def set_configuration_variables(cls, configuration):
        config_file_raw = open(configuration)
        config_file_json = json.load(config_file_raw)

        base_url = config_file_json["hostname"] + "v1/students"
        cls.osu_id = config_file_json["osu_id"]
        cls.url_with_id = f"{base_url}/{cls.osu_id}"

        client_id = config_file_json["client_id"]
        client_secret = config_file_json["client_secret"]

        access_token = cls.get_access_token(config_file_json["token_api"],
                                            client_id, client_secret)

        # Set headers and query parameters
        cls.auth_header = {'Authorization': 'Bearer {}'.format(access_token)}

    @classmethod
    def load_open_api_spec(cls, open_api_spec):
        with open(open_api_spec, 'r') as stream:
            cls.api_spec = yaml.load(stream)

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

    def __make_request(self, endpoint, response_time, params=None):
        url = f"{self.url_with_id}/{endpoint}"
        request = requests.get(url, params=params, headers=self.auth_header)

        self.assert_response_time(request, response_time)
        self.assertEqual(url, request.json()["links"]["self"])

        return request

    def assert_date_format(self, date, date_format):
        try:
            datetime.strptime(date, date_format)
        except ValueError:
            self.fail(f"{date} does not match the format {date_format}")

    def assert_response_time(self, request, max_elapsed_seconds):
        elapsed_seconds = request.elapsed.total_seconds()
        logging.debug("Request took {} second(s)".format(elapsed_seconds))
        self.assertLess(elapsed_seconds, max_elapsed_seconds)

    def assert_object_matches_spec(self, properties, actual):
        self.assertEqual(len(properties), len(actual))

        for field, field_properties in properties.items():
            expected_type = self.__openapi_type(field_properties)
            self.assertIsInstance(actual[field], expected_type)

            if expected_type == str and "format" in field_properties:
                format = field_properties["format"]
                if format == "date-time":
                    self.assert_date_format(actual[field],
                                            "%Y-%m-%dT%H:%M:%SZ")
                elif format == "date":
                    self.assert_date_format(actual[field], "%Y-%m-%d")

    @staticmethod
    def __openapi_type(properties):
        plain_type = properties["type"]

        if plain_type == "string":
            return str
        elif plain_type == "integer":
            return int
        elif plain_type == "number":
            if properties["format"] == "float":
                return float
        elif plain_type == "boolean":
            return bool
        elif plain_type == "array":
            return list

    def __get_properties(self, object_title):
        return self.api_spec["definitions"][object_title]["properties"]

    def test_account_balance(self):
        request = self.__make_request("account-balance", 4)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties("AccountBalanceResultObject")[
            "data"]["properties"]["attributes"]["properties"]

        self.assert_object_matches_spec(properties, attributes)

    def test_account_transactions(self):
        request = self.__make_request("account-transactions", 4)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties("AccountTransactionsResultObject")[
            "data"]["properties"]["attributes"]["properties"]

        self.assert_object_matches_spec(properties, attributes)

        for transaction in attributes["transactions"]:
            self.assert_object_matches_spec(
                properties["transactions"]["items"]["properties"], transaction)

    def test_gpa(self):
        request = self.__make_request("gpa", 4)
        resource_object = request.json()["data"]
        self.assertEqual(self.osu_id, resource_object["id"])

        attributes = resource_object["attributes"]

        properties = self.__get_properties("GradePointAverageResultObject")[
            "data"]["properties"]["attributes"]["properties"]

        self.assert_object_matches_spec(properties, attributes)

        for level in attributes["gpaLevels"]:
            self.assert_object_matches_spec(
                self.__get_properties("GradePointAverageObject"), level)

    def test_academic_status(self):
        request = self.__make_request("academic-status", 4)
        resource_objects = request.json()["data"]

        for resource_object in resource_objects:
            attributes = resource_object["attributes"]
            term = attributes["term"]
            self.assertEqual(f"{self.osu_id}-{term}", resource_object["id"])

            properties = self.__get_properties("AcademicStatusResultObject")[
                "data"]["items"]["properties"]["attributes"]["properties"]

            self.assert_object_matches_spec(properties, attributes)
            for gpa_level in attributes["gpa"]:
                self.assert_object_matches_spec(
                    self.__get_properties("GradePointAverageObject"), gpa_level)



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
    config_file = arguments.config_file
    integration_tests.set_configuration_variables(config_file)

    # Load openAPI yaml file
    open_api = arguments.open_api
    integration_tests.load_open_api_spec(open_api)

    unittest.main(argv=sys.argv[:1] + unittest_args)
