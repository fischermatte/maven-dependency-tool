package org.fischermatte;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public RepositorySystem repositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                exception.printStackTrace();
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    @Bean
    public DefaultRepositorySystemSession repositorySystemSession() {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository("C:\\Users\\georg\\.m2\\repository");
        session.setLocalRepositoryManager(repositorySystem().newLocalRepositoryManager(session, localRepo));
        return session;
    }

    @Bean
    public List<RemoteRepository> remoteRepositories() {
        return new ArrayList<>(Collections.singletonList(mavenCentralRepository()));
    }

    private RemoteRepository mavenCentralRepository() {
        return new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
    }
}
