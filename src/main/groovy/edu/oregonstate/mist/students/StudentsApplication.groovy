package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.students.db.HttpStudentsDAO
import edu.oregonstate.mist.students.db.StudentsDAO
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Environment
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.BasicCredentialsProvider
import org.skife.jdbi.v2.DBI

/**
 * Main application class.
 */
class StudentsApplication extends Application<StudentsConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(StudentsConfiguration configuration, Environment environment) {
        this.setup(configuration, environment)

        DBIFactory factory = new DBIFactory()
        DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "jdbi")
        StudentsDAO studentsDAO = jdbi.onDemand(StudentsDAO.class)

        HttpStudentsDAO httpStudentsDAO = new HttpStudentsDAO(
                getHttpClient(configuration, environment),
                configuration.httpDataSource.endpoint
        )

        StudentsDAOWrapper studentsDAOWrapper = new StudentsDAOWrapper(studentsDAO, httpStudentsDAO)
        environment.jersey().register(new StudentsResource(
                studentsDAOWrapper, configuration.api.endpointUri))

    }

    HttpClient getHttpClient(StudentsConfiguration configuration, Environment environment) {
        def httpClientBuilder = new HttpClientBuilder(environment)

        if (configuration.httpClient != null) {
            httpClientBuilder.using(configuration.httpClient)
        }

        CredentialsProvider provider = new BasicCredentialsProvider()
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                configuration.httpDataSource.username,
                configuration.httpDataSource.password
        )
        provider.setCredentials(AuthScope.ANY, credentials)

        httpClientBuilder.using(provider)

        httpClientBuilder.build("backend-http-client")
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {
        new StudentsApplication().run(arguments)
    }
}
