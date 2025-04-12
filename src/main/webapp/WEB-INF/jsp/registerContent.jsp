<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h4 class="mb-0"><spring:message code="nav.register"/></h4>
            </div>
            <div class="card-body">
                <form:form action="/register" method="post" modelAttribute="user">
                    <div class="mb-3">
                        <label for="username" class="form-label"><spring:message code="user.username"/></label>
                        <form:input path="username" type="text" class="form-control" id="username" required="true" />
                        <form:errors path="username" cssClass="text-danger" />
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label"><spring:message code="user.password"/></label>
                        <form:password path="password" class="form-control" id="password" required="true" />
                        <form:errors path="password" cssClass="text-danger" />
                    </div>
                    <div class="mb-3">
                        <label for="fullName" class="form-label"><spring:message code="user.fullName"/></label>
                        <form:input path="fullName" type="text" class="form-control" id="fullName" required="true" />
                        <form:errors path="fullName" cssClass="text-danger" />
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label"><spring:message code="user.email"/></label>
                        <form:input path="email" type="email" class="form-control" id="email" required="true" />
                        <form:errors path="email" cssClass="text-danger" />
                    </div>
                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label"><spring:message code="user.phoneNumber"/></label>
                        <form:input path="phoneNumber" type="tel" class="form-control" id="phoneNumber" />
                        <form:errors path="phoneNumber" cssClass="text-danger" />
                    </div>
                    <div class="mb-3">
                        <button type="submit" class="btn btn-primary"><spring:message code="nav.register"/></button>
                    </div>
                </form:form>
                
                <div class="mt-3">
                    <p><spring:message code="user.haveAccount"/> <a href="/login"><spring:message code="nav.login"/></a></p>
                </div>
            </div>
        </div>
    </div>
</div> 