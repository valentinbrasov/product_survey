package com.valib.productsurvey;

import com.valib.productsurvey.domain.Product;
import com.valib.productsurvey.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UUID.class, ProductSurveyAdminService.class})
public class ProductSurveyAdminServiceTest {
    private UUID productId = UUID.randomUUID();

    private UUID question1Id = UUID.randomUUID();
    private UUID question2Id = UUID.randomUUID();
    private UUID question3Id = UUID.randomUUID();
    private UUID question4Id = UUID.randomUUID();

    private ProductSurveyAdminService productSurveyAdminService;

    @Before
    public void setup() {
        final Map<UUID, Product> productMap = new HashMap<UUID, Product>() {{
           put(productId, new Product(productId, "Tesla Model S"));
        }};
        productSurveyAdminService = new ProductSurveyAdminService(productMap);

        mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(question1Id).thenReturn(question2Id).thenReturn(question3Id).thenReturn(question4Id);
    }

    @Test
    public void addQuestionsForProduct() {
        // WHEN
        final String question1Text = "Beautiful design?";
        productSurveyAdminService.appendQuestion(productId, question1Text);
        final String question2Text = "Long enough range?";
        productSurveyAdminService.appendQuestion(productId, question2Text);

        // THEN
        assertEquals(2, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestions().size());
        assertEquals(question1Text, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(
                question1Id).getText());
        assertEquals(new Integer(1), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(
                question1Id).getOrderIndex());
        assertEquals(question2Text, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(
                question2Id).getText());
        assertEquals(new Integer(2), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(
                question2Id).getOrderIndex());
    }

    @Test(/* THEN */ expected = IllegalArgumentException.class)
    public void cannotAddQuestionForNonExistingProduct() {
        // GIVEN + WHEN
        productSurveyAdminService.appendQuestion(UUID.randomUUID(), "Beautiful design?");
    }

    @Test
    public void swapQuestionsForProduct() {
        // GIVEN
        Question question1 = productSurveyAdminService.appendQuestion(productId, "Q1 text");
        productSurveyAdminService.appendQuestion(productId, "Q2 text");
        Question question3 = productSurveyAdminService.appendQuestion(productId, "Q3 text");
        productSurveyAdminService.appendQuestion(productId, "Q4 text");

        // WHEN
        productSurveyAdminService.swapQuestions(productId, question1, question3);

        // THEN
        assertEquals(4, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestions().size());
        assertEquals(new Integer(3), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question1Id).getOrderIndex());
        assertEquals(new Integer(2), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question2Id).getOrderIndex());
        assertEquals(new Integer(1), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question3Id).getOrderIndex());
        assertEquals(new Integer(4), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question4Id).getOrderIndex());
    }

    @Test
    public void deleteQuestionForProduct() {
        productSurveyAdminService.appendQuestion(productId, "Q1 text");
        productSurveyAdminService.appendQuestion(productId, "Q2 text");
        productSurveyAdminService.appendQuestion(productId, "Q3 text");

        // WHEN
        productSurveyAdminService.deleteQuestion(productId, question2Id);

        // THEN
        assertEquals(2, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestions().size());
        assertEquals(new Integer(1), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question1Id).getOrderIndex());
        assertEquals(new Integer(3), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question3Id).getOrderIndex());
    }

    @Test
    public void updateQuestionTextForProduct() {
        final String question1Text = "Q1 text";
        productSurveyAdminService.appendQuestion(productId, question1Text);
        productSurveyAdminService.appendQuestion(productId, "Q2 text");

        // WHEN
        final String question2UpdatedText = "Q2 text updated";
        productSurveyAdminService.updateQuestionText(productId, question2Id, question2UpdatedText);

        // THEN
        assertEquals(2, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestions().size());
        assertEquals(new Integer(1), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question1Id).getOrderIndex());
        assertEquals(question1Text, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(
                question1Id).getText());
        assertEquals(new Integer(2), productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(question2Id).getOrderIndex());
        assertEquals(question2UpdatedText, productSurveyAdminService.getQuestionSetForProduct(productId).getQuestion(
                question2Id).getText());
    }

    @Test(/* THEN */ expected = IllegalArgumentException.class)
    public void cannotUpdateTextForNonExistingQuestionForProduct() {
        productSurveyAdminService.appendQuestion(productId, "Q1 text");

        // WHEN
        productSurveyAdminService.updateQuestionText(productId, question2Id, "Q2 text updated");
    }

    @Test(/* THEN */ expected = IllegalArgumentException.class)
    public void cannotDeleteNonExistingQuestionForProduct() {
        productSurveyAdminService.appendQuestion(productId, "Q1 text");

        // WHEN
        productSurveyAdminService.deleteQuestion(productId, question2Id);
    }
}
