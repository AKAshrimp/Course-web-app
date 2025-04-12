<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="card">
    <div class="card-header">
        <h4 class="mb-0"><spring:message code="lecture.add.title"/></h4>
    </div>
    <div class="card-body">
        <form:form action="/admin/lectures/add" method="post" modelAttribute="lecture">
            <div class="mb-3">
                <label for="title" class="form-label"><spring:message code="lecture.title"/></label>
                <form:input path="title" id="title" class="form-control" required="true" />
                <form:errors path="title" cssClass="text-danger" />
            </div>
            <div class="mb-3">
                <label for="description" class="form-label"><spring:message code="lecture.description"/></label>
                <form:textarea path="description" id="description" class="form-control" rows="5" />
                <form:errors path="description" cssClass="text-danger" />
            </div>
            <div class="mb-3">
                <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
                <a href="/index" class="btn btn-secondary"><spring:message code="button.cancel"/></a>
            </div>
        </form:form>
    </div>
</div> 