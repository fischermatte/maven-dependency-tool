package org.fischermatte;

import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private Application application;

	@Test
	public void checkDependencyCompatibility() throws ArtifactDescriptorException, DependencyCollectionException {
		assertNotNull(application);
		application.checkDependencyCompatibility(
				"org.springframework.boot:spring-boot-starter-parent:pom:1.4.2.RELEASE",
				"org.fischermatte:dummy-pom-2:pom:0.0.1-SNAPSHOT");
	}

}
