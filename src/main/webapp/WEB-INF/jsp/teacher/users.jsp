<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../layout/header.jsp">
    <jsp:param name="title" value="用户管理" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                    <h3><i class="bi bi-people"></i> 用户管理</h3>
                    <a href="/teacher/users/add" class="btn btn-light">
                        <i class="bi bi-plus-circle"></i> 添加用户
                    </a>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>用户名</th>
                                    <th>姓名</th>
                                    <th>邮箱</th>
                                    <th>电话</th>
                                    <th>角色</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${users}" var="user">
                                    <tr>
                                        <td>${user.id}</td>
                                        <td>${user.username}</td>
                                        <td>${user.fullName}</td>
                                        <td>${user.email}</td>
                                        <td>${user.phoneNumber}</td>
                                        <td>
                                            <c:forEach items="${user.roles}" var="role" varStatus="status">
                                                <c:choose>
                                                    <c:when test="${role == 'ROLE_TEACHER'}">
                                                        <span class="badge bg-danger">教师</span>
                                                    </c:when>
                                                    <c:when test="${role == 'ROLE_STUDENT'}">
                                                        <span class="badge bg-primary">学生</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">${role}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${!status.last}">&nbsp;</c:if>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="/teacher/users/edit/${user.id}" class="btn btn-sm btn-warning">
                                                    <i class="bi bi-pencil"></i> 编辑
                                                </a>
                                                <form action="/teacher/users/delete/${user.id}" method="post" class="d-inline" 
                                                      onsubmit="return confirm('确定要删除此用户吗？');">
                                                    <button type="submit" class="btn btn-sm btn-danger">
                                                        <i class="bi bi-trash"></i> 删除
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer">
                    <a href="/teacher/dashboard" class="btn btn-secondary">
                        <i class="bi bi-arrow-left"></i> 返回仪表盘
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" /> 