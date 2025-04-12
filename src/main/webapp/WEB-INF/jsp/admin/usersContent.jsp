<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-12">
            <h2><spring:message code="user.management.title" /></h2>
            
            <div class="mb-3">
                <a href="/admin/dashboard" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> <spring:message code="button.back" />
                </a>
            </div>
            
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th><spring:message code="user.id" /></th>
                        <th><spring:message code="user.username" /></th>
                        <th><spring:message code="user.fullName" /></th>
                        <th><spring:message code="user.email" /></th>
                        <th><spring:message code="user.role" /></th>
                        <th><spring:message code="user.actions" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${users}" var="user">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.fullName}</td>
                            <td>${user.email}</td>
                            <td>
                                <c:forEach items="${user.roles}" var="role" varStatus="status">
                                    <spring:message code="role.${role}" />
                                    <c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            </td>
                            <td>
                                <a href="/admin/users/edit/${user.id}" class="btn btn-sm btn-primary">
                                    <i class="bi bi-pencil"></i> <spring:message code="button.edit" />
                                </a>
                                <form action="/admin/users/${user.id}/delete" method="post" style="display: inline;">
                                    <button type="submit" class="btn btn-sm btn-danger" 
                                            onclick="return confirm('<spring:message code="user.delete.confirm" />')">
                                        <i class="bi bi-trash"></i> <spring:message code="button.delete" />
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div> 