package javajedi.com.service;

import javajedi.com.data.PublicationData;
import javajedi.com.enumeration.PublicationType;
import javajedi.com.exception.domain.PublicationNotFoundException;
import javajedi.com.mapper.PublicationMapper;
import javajedi.com.model.Publication;
import javajedi.com.model.Topic;
import javajedi.com.repository.PublicationRepository;
import javajedi.com.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Objects.nonNull;
import static javajedi.com.constant.FileConstant.*;
import static javajedi.com.constant.PublicationConstant.NO_PUBLICATION_FOUND_BY_PUBLICATION_ID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;

    private final PublicationMapper publicationMapper;

    private final TopicRepository topicRepository;

    @Override
    public List<PublicationData> findAllPublications() {
        return publicationMapper.mapUserPublicationList(publicationRepository.findAll());
    }

    @Override
    public PublicationData findPublicationById(String publicationId) {
        Publication publication = publicationRepository.findPublicationByPublicationId(publicationId)
                .orElseThrow(() -> new PublicationNotFoundException(NO_PUBLICATION_FOUND_BY_PUBLICATION_ID + publicationId));
        return publicationMapper.toUserPublicationData(publication);
    }

    @Override
    public List<PublicationData> findAllUserPublications(String username) {
        return publicationMapper.mapPublicationList(publicationRepository.findAllByAuthor(username));
    }

    @Override
    public PublicationData postPublication(String username, String title, String description, Boolean isPublic, MultipartFile file,
                                           String publicationType, List<String> topics) throws IOException {
        Publication publication = new Publication();
        publication.setPublicationId(generatePublicationId());
        publication.setTitle(title);
        publication.setDescription(description);
        publication.setIsPublic(isPublic);
        publication.setPublicationType(PublicationType.valueOf(publicationType.toUpperCase()));
        publication.setAuthor(username);
        topics.forEach(topicName -> topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(
                        publication::addTopic,
                        () -> publication.addTopic(new Topic(topicName))));
        savePublication(username, publication, file);
        publicationRepository.save(publication);
        return publicationMapper.toPublicationData(publication);
    }

    @Override
    public PublicationData editPublication(String publicationId, String title, String description, Boolean isPublic, List<String> topics) {
        Publication publication = publicationRepository.findPublicationByPublicationId(publicationId)
                .orElseThrow(() -> new PublicationNotFoundException(NO_PUBLICATION_FOUND_BY_PUBLICATION_ID + publicationId));
        publication.setDescription(description);
        publication.setIsPublic(isPublic);
        publication.setTitle(title);
        ArrayList<Topic> publicationTopics = new ArrayList<>(publication.getTopics());
        publicationTopics.forEach(publicationTopic -> {
            if (!topics.contains(publicationTopic.getName())) {
                publication.removeTopic(publicationTopic);
            }
        });
        topics.forEach(topicName -> topicRepository.findTopicByName(topicName)
                .ifPresentOrElse(topic -> {
                            if (!publicationTopics.contains(topic)) {
                                publication.addTopic(topic);
                            }
                        },
                        () -> publication.addTopic(new Topic(topicName))));
        publicationRepository.save(publication);
        return publicationMapper.toPublicationData(publication);
    }

    @Override
    public byte[] getPublicationMedia(String username, String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + PUBLICATIONS + FORWARD_SLASH + fileName));
    }

    @Override
    public void deletePublication(String publicationId) {
        publicationRepository.deletePublicationByPublicationId(publicationId);
    }

    private void savePublication(String user, Publication publication, MultipartFile file) throws IOException {
        if (nonNull(file)) {
            Path userFolder = Paths.get(USER_FOLDER + user + FORWARD_SLASH + PUBLICATIONS).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(USER_FOLDER + user + FORWARD_SLASH + user + DOT + JPG_EXTENSION));
            Files.copy(file.getInputStream(), userFolder.resolve(publication.getPublicationId() + DOT + PDF_EXTENSION), REPLACE_EXISTING);
            publication.setStoragePath(setStoragePath(user, publication.getPublicationId()));
        }
    }

    private String setStoragePath(String username, String publicationId) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(PUBLICATION_MEDIA_PATH
                + FORWARD_SLASH + username + FORWARD_SLASH + publicationId + DOT + PDF_EXTENSION).toUriString();
    }

    private String generatePublicationId() {
        return randomAlphanumeric(10) + publicationRepository.count();
    }

}
