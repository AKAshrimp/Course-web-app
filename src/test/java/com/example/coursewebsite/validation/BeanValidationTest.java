package com.example.coursewebsite.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.example.coursewebsite.controller.LectureController;
import com.example.coursewebsite.controller.PollController;
import com.example.coursewebsite.model.Lecture;
import com.example.coursewebsite.model.Poll;
import com.example.coursewebsite.model.PollOption;
import com.example.coursewebsite.model.User;
import com.example.coursewebsite.service.CommentService;
import com.example.coursewebsite.service.PollService;
import com.example.coursewebsite.service.UserService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class BeanValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void rejectsInvalidUserRegistrationFields() {
        User user = new User();
        user.setUsername(" ");
        user.setPassword("123");
        user.setFullName(" ");
        user.setEmail("not-an-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .contains("username", "password", "fullName", "email");
    }

    @Test
    void acceptsValidUserRegistrationFields() {
        User user = new User("student1", "password123", "Student One", "student@example.com", "12345678");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isEmpty();
    }

    @Test
    void rejectsInvalidLectureFields() {
        Lecture lecture = new Lecture();
        lecture.setTitle(" ");
        lecture.setDescription(" ");

        Set<ConstraintViolation<Lecture>> violations = validator.validate(lecture);

        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .contains("title", "description");
    }

    @Test
    void rejectsInvalidPollFields() {
        Poll poll = new Poll();
        poll.setQuestion(" ");
        poll.addOption(new PollOption(" ", poll));

        Set<ConstraintViolation<Poll>> violations = validator.validate(poll);

        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .contains("question", "options[0].text");
    }

    @Test
    void lectureFormHandlersUseBeanValidation() throws NoSuchMethodException {
        Method addLecture = LectureController.class.getMethod("addLecture", Lecture.class, BindingResult.class,
                org.springframework.web.servlet.mvc.support.RedirectAttributes.class);
        Method updateLecture = LectureController.class.getMethod("updateLecture", Long.class, Lecture.class,
                BindingResult.class, org.springframework.web.servlet.mvc.support.RedirectAttributes.class);

        assertValidatedModelAttribute(addLecture, 0, "lecture");
        assertValidatedModelAttribute(updateLecture, 1, "lecture");
    }

    @Test
    void pollFormHandlersUseBeanValidation() throws NoSuchMethodException {
        Method addPoll = PollController.class.getMethod("addPoll", Poll.class, BindingResult.class, java.util.List.class,
                org.springframework.web.servlet.mvc.support.RedirectAttributes.class);
        Method updatePoll = PollController.class.getMethod("updatePoll", Long.class, Poll.class, BindingResult.class,
                java.util.List.class, java.util.List.class, java.util.List.class,
                org.springframework.web.servlet.mvc.support.RedirectAttributes.class);

        assertValidatedModelAttribute(addPoll, 0, "poll");
        assertValidatedModelAttribute(updatePoll, 1, "poll");
    }

    @Test
    void pollOptionUpdateDoesNotRequireQuestionField() {
        PollService pollService = mock(PollService.class);
        PollController controller = new PollController(pollService, mock(CommentService.class), mock(UserService.class));
        Poll existingPoll = new Poll("Existing question");
        when(pollService.getPollById(1L)).thenReturn(Optional.of(existingPoll));
        Poll submittedPoll = new Poll();
        BindingResult result = new BeanPropertyBindingResult(submittedPoll, "poll");
        result.rejectValue("question", "NotBlank");

        String view = controller.updatePoll(1L, submittedPoll, result, List.of("Updated option"), List.of(2L), null,
                new RedirectAttributesModelMap());

        assertThat(view).isEqualTo("redirect:/admin/dashboard");
        assertThat(existingPoll.getQuestion()).isEqualTo("Existing question");
        verify(pollService).updatePollOption(2L, "Updated option");
        verify(pollService).updatePoll(existingPoll);
    }

    private void assertValidatedModelAttribute(Method method, int parameterIndex, String modelAttributeName) {
        assertThat(method.getParameters()[parameterIndex].isAnnotationPresent(Valid.class)).isTrue();
        ModelAttribute modelAttribute = method.getParameters()[parameterIndex].getAnnotation(ModelAttribute.class);
        assertThat(modelAttribute).isNotNull();
        assertThat(modelAttribute.value()).isEqualTo(modelAttributeName);
        assertThat(method.getParameterTypes()[parameterIndex + 1]).isEqualTo(BindingResult.class);
    }
}
