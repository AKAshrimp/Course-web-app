<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- 操作提示区域 -->
<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle-fill"></i> <spring:message code="${successMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="row mb-4">
    <div class="col-12">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0"><i class="bi bi-speedometer2"></i> <spring:message code="teacher.badge"/> <spring:message code="teacher.dashboard"/></h4>
            </div>
            <div class="card-body">
                <div class="alert alert-info">
                    <spring:message code="teacher.welcome"/>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row mb-4">
    <!-- 用户管理 -->
    <div class="col-md-6 mb-4">
        <div class="card h-100">
            <div class="card-header bg-success text-white">
                <h5 class="mb-0"><i class="bi bi-people"></i> <spring:message code="teacher.manage.users"/></h5>
            </div>
            <div class="card-body">
                <div class="list-group">
                    <c:forEach items="${allUsers}" var="user">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6>${user.fullName}</h6>
                                <small class="text-muted">${user.email}</small>
                                <c:if test="${user.roles.contains('ROLE_TEACHER')}">
                                    <span class="badge bg-danger ms-2"><spring:message code="teacher.badge"/></span>
                                </c:if>
                            </div>
                            <div>
                                <a href="/admin/users/edit/${user.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <form method="post" action="/admin/users/${user.id}/delete" style="display:inline;">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" 
                                       onclick="return confirm('<spring:message code="confirm.delete.user"/>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <a href="/admin/users/add" class="btn btn-success mt-3">
                    <i class="bi bi-plus-circle"></i> <spring:message code="teacher.add.user"/>
                </a>
            </div>
        </div>
    </div>
    
    <!-- 课程管理 -->
    <div class="col-md-6 mb-4">
        <div class="card h-100">
            <div class="card-header bg-info text-white">
                <h5 class="mb-0"><i class="bi bi-book"></i> <spring:message code="teacher.manage.courses"/></h5>
            </div>
            <div class="card-body">
                <div class="list-group">
                    <c:forEach items="${lectures}" var="lecture">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6>${lecture.title}</h6>
                                <small class="text-muted">${lecture.description}</small>
                            </div>
                            <div>
                                <a href="/lecture/${lecture.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="/admin/lectures/edit/${lecture.id}" class="btn btn-sm btn-outline-info">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <form method="post" action="/admin/lectures/${lecture.id}/delete" style="display:inline;">
                                    <button type="submit" class="btn btn-sm btn-outline-danger"
                                       onclick="return confirm('<spring:message code="confirm.delete.course"/>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <a href="/admin/lectures/add" class="btn btn-info mt-3 text-white">
                    <i class="bi bi-plus-circle"></i> <spring:message code="teacher.add.course"/>
                </a>
            </div>
        </div>
    </div>
    
    <!-- 投票管理 -->
    <div class="col-md-12">
        <div class="card h-100">
            <div class="card-header bg-warning text-dark">
                <h5 class="mb-0"><i class="bi bi-bar-chart"></i> <spring:message code="teacher.manage.polls"/></h5>
            </div>
            <div class="card-body">
                <div class="list-group">
                    <c:forEach items="${polls}" var="poll">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <h6>${poll.question}</h6>
                                <small class="text-muted">${poll.votes.size()} <spring:message code="home.votes"/></small>
                            </div>
                            <div>
                                <a href="/poll/${poll.id}" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-eye"></i>
                                </a>
                                <a href="/admin/polls/edit/${poll.id}" class="btn btn-sm btn-outline-warning">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <form method="post" action="/admin/polls/${poll.id}/delete" style="display:inline;">
                                    <button type="submit" class="btn btn-sm btn-outline-danger"
                                       onclick="return confirm('<spring:message code="confirm.delete.poll"/>')">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <a href="/admin/polls/add" class="btn btn-warning mt-3">
                    <i class="bi bi-plus-circle"></i> <spring:message code="teacher.add.poll"/>
                </a>
            </div>
        </div>
    </div>
</div> 