package com.example.ragebait.repository

import com.example.ragebait.entity.RagebaitPost
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.Test

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RagebaitRepositoryTest @Autowired constructor(
    val ragebaitRepository: RagebaitRepository
) {

    @Test
    fun `should save and retrieve ragebait`() {
        val topic = "AI taking over"
        val ragebait = RagebaitPost(topic = topic, content = "It's the end of human creativity")
        val saved = ragebaitRepository.save(ragebait)

        val foundList = ragebaitRepository.findByTopicContainingIgnoreCase(saved.topic)
        val found = ragebaitRepository.findById(saved.id)

        assertThat(foundList).isNotEmpty
        assertThat(found).isPresent
        assertThat(found.get().topic).isEqualTo("AI taking over")
        assertThat(found.get().content).contains("human creativity")
    }

    @Test
    fun `should return empty list for non-existent topic`() {
        val foundList = ragebaitRepository.findByTopicContainingIgnoreCase("nonexistent")
        assertThat(foundList).isEmpty()
    }

    @Test
    fun `should find posts case-insensitively`() {
        val topic = "Case Sensitive"
        val ragebait = RagebaitPost(topic = topic, content = "Test content")
        ragebaitRepository.save(ragebait)

        val foundList = ragebaitRepository.findByTopicContainingIgnoreCase("case sensitive")
        assertThat(foundList).isNotEmpty
        assertThat(foundList[0].topic).isEqualTo(topic)
    }

    @Test
    fun `should find multiple posts with partial topic match`() {
        val topic1 = "AI and Machine Learning"
        val topic2 = "AI in Healthcare"
        val ragebait1 = RagebaitPost(topic = topic1, content = "Content 1")
        val ragebait2 = RagebaitPost(topic = topic2, content = "Content 2")
        
        ragebaitRepository.save(ragebait1)
        ragebaitRepository.save(ragebait2)

        val foundList = ragebaitRepository.findByTopicContainingIgnoreCase("AI")
        assertThat(foundList).hasSize(2)
        assertThat(foundList.map { it.topic }).containsExactlyInAnyOrder(topic1, topic2)
    }

    @Test
    fun `should delete post`() {
        val topic = "To be deleted"
        val ragebait = RagebaitPost(topic = topic, content = "Will be deleted")
        val saved = ragebaitRepository.save(ragebait)

        ragebaitRepository.deleteById(saved.id)
        val found = ragebaitRepository.findById(saved.id)
        assertThat(found).isEmpty
    }
}