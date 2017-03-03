package com.luka;

import com.luka.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by luciferche on 3/2/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestTest {


        private TestRestTemplate restTemplate = new TestRestTemplate();

        @Test
        public void test() {
            ResponseEntity<ArrayList> users = this.restTemplate.getForEntity(
                    "/users/", ArrayList.class);
            System.out.println("users " + users.getBody().toString());
        }

}
