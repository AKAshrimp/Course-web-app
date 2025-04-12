<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h4 class="mb-0">${poll.question}</h4>
        <sec:authorize access="hasRole('TEACHER')">
            <form action="/admin/polls/${poll.id}/delete" method="post">
                <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('<spring:message code="poll.confirmDeletePoll"/>')"><spring:message code="course.delete"/></button>
            </form>
        </sec:authorize>
    </div>
    <div class="card-body">
        <p><spring:message code="poll.createdAt"/>: ${poll.createdAt}</p>
        <p><spring:message code="poll.totalVotes"/>: ${poll.votes.size()}</p>
        
        <c:if test="${showVoteAlert}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <spring:message code="${voteMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <sec:authorize access="isAuthenticated()">
            <form action="/poll/${poll.id}/vote" method="post">
                <div class="list-group mb-3">
                    <c:forEach items="${poll.options}" var="option">
                        <div class="list-group-item">
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="optionId" id="option${option.id}" value="${option.id}" 
                                    <c:if test="${userVote != null && userVote.pollOption.id == option.id}">checked</c:if>>
                                <label class="form-check-label d-flex justify-content-between" for="option${option.id}">
                                    <span>${option.text}</span>
                                    <span class="badge bg-primary rounded-pill">
                                        <c:set var="voteCount" value="${poll.getVoteCount(option.id)}" />
                                        ${voteCount} <spring:message code="home.votes"/> 
                                        <c:if test="${poll.votes.size() > 0}">
                                            (<fmt:formatNumber value="${(voteCount / poll.votes.size()) * 100}" pattern="#.##" />%)
                                        </c:if>
                                    </span>
                                </label>
                            </div>
                            <div class="progress mt-2" role="progressbar">
                                <c:set var="percentage" value="${poll.votes.size() > 0 ? (poll.getVoteCount(option.id) / poll.votes.size()) * 100 : 0}" />
                                <div class="progress-bar" style="width: ${percentage}%"></div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <button type="submit" class="btn btn-primary mb-4"><spring:message code="poll.vote"/></button>
            </form>
        </sec:authorize>
        
        <sec:authorize access="!isAuthenticated()">
            <div class="alert alert-info">
                <spring:message code="poll.loginToVote"/>
            </div>
        </sec:authorize>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h5 class="mb-0"><spring:message code="course.comments"/></h5>
    </div>
    <div class="card-body">
        <sec:authorize access="isAuthenticated()">
            <form action="/poll/${poll.id}/comment" method="post" class="mb-4">
                <div class="mb-3">
                    <textarea class="form-control" name="content" rows="3" placeholder="<spring:message code="course.writeComment"/>" required></textarea>
                </div>
                <div class="mb-3">
                    <button type="submit" class="btn btn-primary"><spring:message code="course.submitComment"/></button>
                </div>
            </form>
        </sec:authorize>
        
        <c:choose>
            <c:when test="${empty comments}">
                <p class="text-muted"><spring:message code="course.noComments"/></p>
            </c:when>
            <c:otherwise>
                <div class="list-group">
                    <c:forEach items="${comments}" var="comment">
                        <div class="list-group-item">
                            <div class="d-flex justify-content-between">
                                <h6 class="mb-1">${comment.user.fullName}</h6>
                                <small class="text-muted">${comment.createdAt}</small>
                            </div>
                            <p class="mb-1">${comment.content}</p>
                            <sec:authorize access="hasRole('TEACHER')">
                                <form action="/admin/poll/comment/${comment.id}/delete" method="post">
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('<spring:message code="poll.confirmDeleteComment"/>')"><spring:message code="course.delete"/></button>
                                </form>
                            </sec:authorize>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div> 