package com.max.appengine.springboot.megaiq.unit.service;

import java.util.ArrayList;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.max.appengine.springboot.megaiq.Application;
import com.max.appengine.springboot.megaiq.model.Answer;
import com.max.appengine.springboot.megaiq.model.Question;
import com.max.appengine.springboot.megaiq.model.enums.IqQuestionGroup;
import com.max.appengine.springboot.megaiq.model.enums.Locale;
import com.max.appengine.springboot.megaiq.repository.AnswerReporitory;
import com.max.appengine.springboot.megaiq.repository.QuestionReporitory;
import com.max.appengine.springboot.megaiq.service.QuestionsService;
import com.max.appengine.springboot.megaiq.unit.AbstractUnitTest;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest(classes = Application.class)
public class TestQuestionsService extends AbstractUnitTest {
	@Autowired
	private AnswerReporitory answerReporitory;

	@Autowired
	private QuestionReporitory questionReporitory;

	@Before
	public void doSetup() {
		for (int i = 1; i <= 5; i++) {
			// 3 answers for each question
			// second answer is correct
			for (int j = 1; j <= 3; j++) {
				answerReporitory.save(new Answer((i-1)*6+j, "test.en q" + i + "a" + j, i, new Date(), new Date(), Locale.EN));
				answerReporitory.save(new Answer((i-1)*6+j, "test.de q" + i + "a" + j, i, new Date(), new Date(), Locale.DE));
				answerReporitory.save(new Answer((i-1)*6+j, "test.ru q" + i + "a" + j, i, new Date(), new Date(), Locale.RU));
			}
			
			questionReporitory.save(new Question(i, "pic", 1, (i-1)*6+2, "test.en q" + i, "info", new ArrayList<IqQuestionGroup>(), new Date(), new Date(), Locale.EN));
		}

	}

	@Test
	public void testInitService() {
		log.info("answerReporitory={}", answerReporitory.findAll());
		log.info("questionReporitory={}", questionReporitory.findAll());

		QuestionsService questionsService = new QuestionsService(answerReporitory, questionReporitory);
		log.info("questionsService={}", questionsService);
	}

}
