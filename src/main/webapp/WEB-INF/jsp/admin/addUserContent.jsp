<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h3><i class="bi bi-person-plus"></i> <spring:message code="admin.add.user" /></h3>
                </div>
                <div class="card-body">
                    <form:form method="post" modelAttribute="user" action="/admin/users/add" class="needs-validation">
                        <div class="mb-3">
                            <label for="username" class="form-label"><spring:message code="user.username" /></label>
                            <form:input path="username" id="username" class="form-control" required="true" />
                            <form:errors path="username" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label for="password" class="form-label"><spring:message code="user.password" /></label>
                            <form:password path="password" id="password" class="form-control" required="true" />
                            <form:errors path="password" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label for="fullName" class="form-label"><spring:message code="user.fullName" /></label>
                            <form:input path="fullName" id="fullName" class="form-control" required="true" />
                            <form:errors path="fullName" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label for="email" class="form-label"><spring:message code="user.email" /></label>
                            <form:input path="email" id="email" type="email" class="form-control" required="true" />
                            <form:errors path="email" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label for="phoneNumber" class="form-label"><spring:message code="user.phoneNumber" /></label>
                            <form:input path="phoneNumber" id="phoneNumber" class="form-control" required="true" />
                            <form:errors path="phoneNumber" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label"><spring:message code="user.roles" /></label>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="roles" value="ROLE_STUDENT" id="roleStudent" checked>
                                <label class="form-check-label" for="roleStudent">
                                    <spring:message code="role.student" />
                                </label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="roles" value="ROLE_TEACHER" id="roleTeacher">
                                <label class="form-check-label" for="roleTeacher">
                                    <spring:message code="role.teacher" />
                                </label>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-save"></i> <spring:message code="button.save" />
                            </button>
                            <a href="/admin/dashboard" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> <spring:message code="button.back" />
                            </a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div> 