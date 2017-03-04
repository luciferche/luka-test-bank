package com.luka;

import com.luka.model.MoneyTransaction;
import com.luka.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.ws.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by luciferche on 3/2/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestTest {


        private TestRestTemplate restTemplate;// = new TestRestTemplate();
        // This will hold the port number the server was started on
        @Value("${local.server.port}")
        int port;

        @Before
        public void setupTest(){
        }

        /**
         * Test basic api get : [/api/users]
         */
        @Test
        public void test() {
                restTemplate = new TestRestTemplate("user","1234");
                String uri = "http://localhost:" + port + "/api/users";
            ResponseEntity<ArrayList> users = this.restTemplate.getForEntity(
                    uri, ArrayList.class);

                assertThat(users.getBody().size()).isEqualTo(3  );
                assertThat(users.getStatusCode()).isEqualTo(HttpStatus.OK);
                System.out.println("users " + users.getBody().toString());
        }

        /**
         * Testing creating user api
         */
        @Test
        public void testPostUser() {
                restTemplate = new TestRestTemplate("luka.matovic","1234");
                String uri = "http://localhost:" + port + "/api/users";
                ResponseEntity<ArrayList> users = this.restTemplate.getForEntity(
                        uri, ArrayList.class);
                User user = new User("luka", "1234");

                ResponseEntity<Void> response = this.restTemplate.postForEntity(uri, user, Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                response = this.restTemplate.postForEntity(uri, user, Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

                user = new User("luka", "");
                response = this.restTemplate.postForEntity(uri, user, Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

                user = new User("", "1234");
                response = this.restTemplate.postForEntity(uri, user, Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        }

        /**
         * Testing get one user api
         */
        @Test
        public void testGetUser() {
                restTemplate = new TestRestTemplate("luka.matovic","1234");
                String uri = "http://localhost:" + port + "/api/users";

                ResponseEntity<User> response = this.restTemplate.getForEntity(
                        uri + "/1", User.class);

                assertThat(response.getBody().getEmail()).isEqualTo("luka.matovic");

                response = this.restTemplate.getForEntity(
                        uri + "/133", User.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


        }


        /**
         * Testing making deposit
         */
        @Test
        public void testPostDeposit() {
                restTemplate = new TestRestTemplate("luka.matovic","1234");
                String uri = "http://localhost:" + port + "/api/users";
                String correctUri = uri + "/1/deposit?amount=";
                String someUri = uri + "/123/deposit?amount=2";

                ResponseEntity<Void> response = this.restTemplate.postForEntity(correctUri+"2.22", "", Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                response = this.restTemplate.postForEntity(correctUri+"2.3", "", Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                response = this.restTemplate.postForEntity(correctUri, "", Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

                ResponseEntity<User> responseGetUser = this.restTemplate.getForEntity(
                        uri + "/1", User.class);


                assertThat(responseGetUser.getBody().getBalance()).isEqualTo(BigDecimal.valueOf(4.52));

                response = this.restTemplate.postForEntity(someUri, 33.333, Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);



        }


        /**
         * Testing making deposit
         */
        @Test
        public void testPostWithdrawal() {
                restTemplate = new TestRestTemplate("luka.matovic","1234");
                String uri = "http://localhost:" + port + "/api/users";
                String correctUri = uri + "/2/withdraw?amount=";
                String someUri = uri + "/123/withdraw?amount=2";
                String depositUri = uri + "/2/deposit?amount=";

                ResponseEntity<Void> response = this.restTemplate.postForEntity(correctUri+"2.22", "", Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                response = this.restTemplate.postForEntity(depositUri+"2.35", "", Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                response = this.restTemplate.postForEntity(correctUri+"2", "", Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                ResponseEntity<User> responseGetUser = this.restTemplate.getForEntity(
                        uri + "/2", User.class);
                assertThat(responseGetUser.getBody().getBalance()).isEqualTo(BigDecimal.valueOf(0.35));
//
//
                response = this.restTemplate.postForEntity(someUri, 33.333, Void.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);



        }


        /**
         * Testing get one transaction
         */
        @Test
        public void testGetTransaction() {
                restTemplate = new TestRestTemplate("luka.matovic","1234");
                String uri = "http://localhost:" + port + "/api/transactions";
                String depositUri = "http://localhost:" + port + "/api/users/3/deposit?amount=2.88";

                //check for non-existing transaction first
                ResponseEntity<MoneyTransaction> response = this.restTemplate.getForEntity(
                        uri+"/1233", MoneyTransaction.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

                //deposit funds, one new transaction
                ResponseEntity<Void> responseDeposit = this.restTemplate.postForEntity(depositUri, "", Void.class);
                assertThat(responseDeposit.getStatusCode()).isEqualTo(HttpStatus.CREATED);


                response = this.restTemplate.getForEntity(
                        uri+"/1", MoneyTransaction.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody().getAmount()).isNotNull();
                assertThat(response.getBody().getTransactionDate()).isNotNull();



        }



}
