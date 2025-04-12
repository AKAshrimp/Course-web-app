<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="text-center my-5">
    <h1 class="fw-bold mb-3 position-relative d-inline-block">
        <span class="position-relative" style="background: linear-gradient(90deg, var(--primary-color), var(--primary-dark)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
            <spring:message code="app.welcome"/>
        </span>
        <div class="position-absolute" style="height: 4px; width: 50%; bottom: -10px; left: 25%; background: linear-gradient(90deg, var(--primary-color), var(--primary-dark));"></div>
    </h1>
    <p class="col-md-8 mx-auto fs-5 text-muted"><spring:message code="app.description"/></p>
    <sec:authorize access="!isAuthenticated()">
        <a href="/register" class="btn btn-primary btn-lg px-4 mt-3">
            <i class="bi bi-person-plus me-2"></i>
            <spring:message code="nav.register"/>
        </a>
    </sec:authorize>
</div>

<div class="row mb-4">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="bi bi-book me-2"></i>
                    <span style="background: linear-gradient(90deg, var(--primary-color), var(--primary-dark)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                        <spring:message code="home.courseList"/>
                    </span>
                </h5>
                <sec:authorize access="hasRole('TEACHER')">
                    <a href="/admin/lectures/add" class="btn btn-sm btn-primary">
                        <i class="bi bi-plus-circle me-1"></i>
                        <spring:message code="nav.addCourse"/>
                    </a>
                </sec:authorize>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty lectures}">
                        <p class="text-muted"><spring:message code="home.noCourses"/></p>
                    </c:when>
                    <c:otherwise>
                        <div class="list-group">
                            <c:forEach items="${lectures}" var="lecture">
                                <sec:authorize access="isAuthenticated()">
                                    <a href="/lecture/${lecture.id}" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1">${lecture.title}</h5>
                                        </div>
                                    </a>
                                </sec:authorize>
                                <sec:authorize access="!isAuthenticated()">
                                    <a href="/login" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1">${lecture.title}</h5>
                                        </div>
                                    </a>
                                </sec:authorize>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <div class="col-md-6">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="bi bi-bar-chart me-2"></i>
                    <span style="background: linear-gradient(90deg, var(--primary-color), var(--primary-dark)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                        <spring:message code="home.pollList"/>
                    </span>
                </h5>
                <sec:authorize access="hasRole('TEACHER')">
                    <a href="/admin/polls/add" class="btn btn-sm btn-primary">
                        <i class="bi bi-plus-circle me-1"></i>
                        <spring:message code="nav.addPoll"/>
                    </a>
                </sec:authorize>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty polls}">
                        <p class="text-muted"><spring:message code="home.noPolls"/></p>
                    </c:when>
                    <c:otherwise>
                        <div class="list-group">
                            <c:forEach items="${polls}" var="poll">
                                <sec:authorize access="isAuthenticated()">
                                    <a href="/poll/${poll.id}" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1"><spring:message code="${poll.question}"/></h5>
                                            <small class="text-muted">
                                                <c:set var="totalVotes" value="${poll.votes.size()}" />
                                                ${totalVotes} <spring:message code="home.votes"/>
                                            </small>
                                        </div>
                                    </a>
                                </sec:authorize>
                                <sec:authorize access="!isAuthenticated()">
                                    <a href="/login" class="list-group-item list-group-item-action">
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1"><spring:message code="${poll.question}"/></h5>
                                            <small class="text-muted">
                                                <c:set var="totalVotes" value="${poll.votes.size()}" />
                                                ${totalVotes} <spring:message code="home.votes"/>
                                            </small>
                                        </div>
                                    </a>
                                </sec:authorize>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div> 