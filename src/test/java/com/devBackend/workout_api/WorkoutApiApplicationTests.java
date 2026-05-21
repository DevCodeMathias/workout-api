package com.devBackend.workout_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.data.mongodb.uri=mongodb://localhost:27017/workout_api_test",
		"mongodb.collections.activities=activityCollectionTest",
		"mongodb.collections.employees=employessCollectionTest"
})
class WorkoutApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
