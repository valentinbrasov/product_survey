package com.valib.productsurvey;

import com.valib.productsurvey.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductSurveyServiceTest {
    private UUID userId = UUID.randomUUID();

    private UUID productId = UUID.randomUUID();

    private UUID question1Id = UUID.randomUUID();
    private UUID question2Id = UUID.randomUUID();

    @InjectMocks
    private ProductSurveyService surveyService;

    @Mock
    private ProductSurveyAdminService productSurveyAdminService;

    @Before
    public void setup() {
        final Map<UUID, Product> productMap = new HashMap<UUID, Product>() {{
            put(productId, new Product(productId, "Tesla Model S"));
        }};

        surveyService = new ProductSurveyService(productMap, productSurveyAdminService);

        ProductQuestionSet productQuestionSet = new ProductQuestionSet(productId);
        productQuestionSet.putQuestion(new Question(question1Id, "Q1 text"));
        productQuestionSet.putQuestion(new Question(question2Id, "Q2 text"));
        when(productSurveyAdminService.getQuestionSetForProduct(eq(productId))).thenReturn(productQuestionSet);
    }

    @Test
    public void createEmptySurvey() {
        // WHEN
        UserProductSurvey userProductSurvey = surveyService.createEmptyUserProductSurvey(userId, productId);

        // THEN
        assertEquals(2, userProductSurvey.getAnswers().size());
        assertEquals(question1Id, userProductSurvey.getAnswers().get(question1Id).getQuestion().getId());
        assertNull(userProductSurvey.getAnswers().get(question1Id).getRating());
        assertEquals(question2Id, userProductSurvey.getAnswers().get(question2Id).getQuestion().getId());
        assertNull(userProductSurvey.getAnswers().get(question2Id).getRating());
        assertEquals(0, surveyService.userProductSurveyStore.size());
    }

    @Test
    public void saveSurvey() {
        // GIVEN
        UserProductSurvey surveyToSave = surveyService.createEmptyUserProductSurvey(userId, productId);

        surveyToSave.getAnswers().get(question1Id).setRating(Rating.GOOD);
        surveyToSave.getAnswers().get(question2Id).setRating(Rating.VERY_GOOD);

        assertEquals(0, surveyService.userProductSurveyStore.size());

        // WHEN
        surveyService.saveUserProductSurvey(surveyToSave);

        // THEN
        assertEquals(1, surveyService.userProductSurveyStore.size());

        UserProductSurvey surveyFromStore = surveyService.getUserProductSurvey(userId, productId);
        assertEquals(surveyToSave.getUserId(), surveyFromStore.getUserId());
        assertEquals(surveyToSave.getProduct().getId(), surveyFromStore.getProduct().getId());
        assertEquals(surveyToSave.getAnswers().get(question1Id).getRating(), surveyFromStore.getAnswers().get(question1Id).getRating());
        assertEquals(surveyToSave.getAnswers().get(question2Id).getRating(), surveyFromStore.getAnswers().get(question2Id).getRating());
    }

    @Test
    public void changeSurvey() {
        // GIVEN
        UserProductSurvey surveyToSave = surveyService.createEmptyUserProductSurvey(userId, productId);

        surveyToSave.getAnswers().get(question1Id).setRating(Rating.GOOD);
        surveyToSave.getAnswers().get(question2Id).setRating(Rating.VERY_GOOD);

        assertEquals(0, surveyService.userProductSurveyStore.size());

        surveyService.saveUserProductSurvey(surveyToSave);
        assertEquals(1, surveyService.userProductSurveyStore.size());
        UserProductSurvey surveyFromStore = surveyService.getUserProductSurvey(userId, productId);

        // WHEN
        surveyFromStore.getAnswers().get(question1Id).setRating(Rating.POOR);
        surveyService.saveUserProductSurvey(surveyFromStore);

        // THEN
        assertEquals(1, surveyService.userProductSurveyStore.size());

        UserProductSurvey surveyFromStoreAgain = surveyService.getUserProductSurvey(userId, productId);
        assertEquals(surveyToSave.getUserId(), surveyFromStoreAgain.getUserId());
        assertEquals(surveyToSave.getProduct().getId(), surveyFromStoreAgain.getProduct().getId());
        assertEquals(Rating.POOR, surveyFromStoreAgain.getAnswers().get(question1Id).getRating());
        assertEquals(Rating.VERY_GOOD, surveyFromStoreAgain.getAnswers().get(question2Id).getRating());
    }
}
