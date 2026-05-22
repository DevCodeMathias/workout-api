package com.devbackend.workoutapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.data.mongodb.uri=mongodb://localhost:27017/workout_api_test",
		"mongodb.collections.activities=activityCollectionTest",
		"mongodb.collections.employees=employessCollectionTest",
		"security.jwt.secret=test-secret-key-with-at-least-32-characters",
		"security.jwt.expiration-hours=24"
})
class WorkoutApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
