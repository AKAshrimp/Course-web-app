<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-lg-10 mx-auto">
            <div class="card shadow-sm">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h4 class="mb-0">
                        <i class="bi bi-clock-history me-2"></i>
                        <spring:message code="voting.history.title"/> - ${user.username}
                    </h4>
                    <a href="javascript:history.back()" class="btn btn-sm btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> <spring:message code="voting.history.back"/>
                    </a>
                </div>
                
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th scope="col" class="ps-4"><spring:message code="voting.history.question"/></th>
                                    <th scope="col"><spring:message code="voting.history.selected"/></th>
                                    <th scope="col"><spring:message code="voting.history.date"/></th>
                                    <th scope="col" class="text-end pe-4"><spring:message code="voting.history.actions"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty votes}">
                                        <tr>
                                            <td colspan="4" class="text-center py-4">
                                                <i class="bi bi-emoji-neutral me-2"></i>
                                                <spring:message code="voting.history.empty"/>
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach items="${votes}" var="vote">
                                            <tr>
                                                <td class="ps-4">
                                                    ${vote.poll.question}
                                                </td>
                                                <td>
                                                    <span class="badge bg-success">
                                                        ${vote.pollOption.text}
                                                    </span>
                                                </td>
                                                <td>
                                                    ${vote.votedAt.format(java.time.format.DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss'))}
                                                </td>
                                                <td class="text-end pe-4">
                                                    <a href="/poll/${vote.poll.id}" class="btn btn-sm btn-outline-primary">
                                                        <i class="bi bi-eye"></i>
                                                        <spring:message code="voting.history.view"/>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div> 