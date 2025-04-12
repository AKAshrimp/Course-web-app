<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="card">
    <div class="card-header">
        <h4 class="mb-0"><spring:message code="votes.title"/></h4>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty votes}">
                <p class="text-muted"><spring:message code="votes.noVotes"/></p>
            </c:when>
            <c:otherwise>
                <div class="list-group">
                    <c:forEach items="${votes}" var="vote">
                        <a href="/poll/${vote.poll.id}" class="list-group-item list-group-item-action">
                            <div class="d-flex w-100 justify-content-between">
                                <h5 class="mb-1"><spring:message code="${vote.poll.question}"/></h5>
                                <small class="text-muted">${vote.votedAt}</small>
                            </div>
                            <p class="mb-1"><spring:message code="votes.yourChoice"/>: 
                                <c:choose>
                                    <c:when test="${vote.pollOption.text.startsWith('poll.')}">
                                        <spring:message code="${vote.pollOption.text}"/>
                                    </c:when>
                                    <c:otherwise>
                                        ${vote.pollOption.text}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div> 