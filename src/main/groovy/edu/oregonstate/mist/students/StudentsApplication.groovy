package edu.oregonstate.mist.students

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.api.Configuration
import edu.oregonstate.mist.students.db.StudentsDAO
import edu.oregonstate.mist.students.db.StudentsDAOWrapper
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Environment
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
        StudentsDAOWrapper studentsDAOWrapper = new StudentsDAOWrapper(studentsDAO)
        environment.jersey().register(new StudentsResource(
                studentsDAOWrapper, configuration.api.endpointUri))

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
