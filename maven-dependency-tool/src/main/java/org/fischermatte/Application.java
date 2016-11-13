package org.fischermatte;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.Assert.notEmpty;

@SpringBootApplication
public class Application {

    @Autowired
    private RepositorySystem repositorySystem;
    @Autowired
    private DefaultRepositorySystemSession repositorySystemSession;
    @Autowired
    private List<RemoteRepository> remoteRepositories;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void checkDependencyCompatibility(String... artifactInfos) throws ArtifactDescriptorException, DependencyCollectionException {
        notEmpty(artifactInfos);

        repositorySystemSession.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
        repositorySystemSession.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);

        for (String artifactInfo : artifactInfos) {
            Artifact artifact = new DefaultArtifact(artifactInfo);
            ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
            descriptorRequest.setArtifact(artifact);
            descriptorRequest.setRepositories(remoteRepositories);
            ArtifactDescriptorResult descriptorResult = repositorySystem.readArtifactDescriptor(repositorySystemSession, descriptorRequest);

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRootArtifact(descriptorResult.getArtifact());
//            collectRequest.setDependencies(descriptorResult.getDependencies());
//            collectRequest.setManagedDependencies(descriptorResult.getManagedDependencies());
            collectRequest.setRepositories(descriptorRequest.getRepositories());

            CollectResult collectResult = repositorySystem.collectDependencies(repositorySystemSession, collectRequest);
            Set<String> dependencies = new HashSet<String>();
            collectDependencies(collectResult.getRoot(), dependencies);
            System.out.println(dependencies);
        }
    }

    private void collectDependencies(DependencyNode node, Set<String> dependencies) {
        for (DependencyNode dependencyNode : node.getChildren()) {
            collectDependencies(dependencyNode, dependencies);
            dependencies.add(node.getArtifact().getGroupId() + ":" + node.getArtifact().getArtifactId() + ":" + node.getArtifact().getVersion());
        }

    }
}
