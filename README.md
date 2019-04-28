# BUILDING
### Building at command line:
```
$ mvn clean install
```

### Building in IDE:
Install Lombok plugin in your IDE (for example in Intellij IDEA, which I used), then import the pom.xml.


# NOTES
### The admin UI will use:
* *com.valib.productsurvey.ProductSurveyAdminService.appendQuestion* to add a new question to the end of the question set of a product.
* *com.valib.productsurvey.ProductSurveyAdminService.swapQuestions* and the swap method to reorder the questions.
* *com.valib.productsurvey.ProductSurveyAdminService.updateQuestionText* to update a question's text.
* *com.valib.productsurvey.ProductSurveyAdminService.deleteQuestion* to delete a question.

### The user UI will use:
* *com.valib.productsurvey.ProductSurveyService.createEmptyUserProductSurvey* to start a survey, that is to get all the questions that the user needs to answer.
* *com.valib.productsurvey.ProductSurveyService.saveUserProductSurvey* to save user's survey with his chosen ratings.
    If the user has already a survey for the given product, then this will overwrite the survey.
    So it effectively allows an user to change his survey for a product.
* *com.valib.productsurvey.ProductSurveyService.getUserProductSurvey* to get an user's survey for a product in order to allow the user to see or change his ratings.

### General notes:
* In a real application I would of course use a database for storing the surveys and in top of it perhaps JPA with Spring Data.
* Also, perhaps in a real application I would use interfaces for services.