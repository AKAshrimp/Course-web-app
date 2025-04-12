<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h4 class="mb-0">${lecture.title}</h4>
        <sec:authorize access="hasRole('TEACHER')">
            <form action="/admin/lectures/${lecture.id}/delete" method="post" class="d-inline">
                <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('<spring:message code="confirm.delete.course"/>')"><spring:message code="course.delete"/></button>
            </form>
        </sec:authorize>
    </div>
    <div class="card-body">
        <p>${lecture.description}</p>
    </div>
</div>

<div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0"><spring:message code="lecture.materials"/></h5>
        <sec:authorize access="hasRole('TEACHER')">
            <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#uploadModal"><spring:message code="lecture.upload"/></button>
        </sec:authorize>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty materials}">
                <p class="text-muted"><spring:message code="lecture.noMaterials"/></p>
            </c:when>
            <c:otherwise>
                <div class="list-group">
                    <c:forEach items="${materials}" var="material">
                        <div class="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <i class="bi bi-file-earmark"></i>
                                ${material.title}
                            </div>
                            <div>
                                <a href="/lecture/material/${material.id}/download" class="btn btn-sm btn-primary"><spring:message code="lecture.download"/></a>
                                <sec:authorize access="hasRole('TEACHER')">
                                    <form action="/admin/lectures/material/${material.id}/delete" method="post" class="d-inline">
                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('<spring:message code="confirm.delete.material"/>')"><spring:message code="course.delete"/></button>
                                    </form>
                                </sec:authorize>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h5 class="mb-0"><spring:message code="course.comments"/></h5>
    </div>
    <div class="card-body">
        <sec:authorize access="isAuthenticated()">
            <form action="/lecture/${lecture.id}/comment" method="post" class="mb-4">
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
                                <form action="/admin/lecture/comment/${comment.id}/delete" method="post">
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('<spring:message code="confirm.delete.comment"/>')"><spring:message code="course.delete"/></button>
                                </form>
                            </sec:authorize>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- 上传模态框 -->
<sec:authorize access="hasRole('TEACHER')">
    <div class="modal fade" id="uploadModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><spring:message code="lecture.uploadMaterial"/></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form action="/admin/lectures/${lecture.id}/upload" method="post" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="title" class="form-label"><spring:message code="lecture.materialTitle"/></label>
                            <input type="text" class="form-control" id="title" name="title" required>
                        </div>
                        <div class="mb-3">
                            <label for="file" class="form-label"><spring:message code="lecture.file"/></label>
                            <input type="file" class="form-control" id="file" name="file" required>
                        </div>
                        <div class="mb-3">
                            <button type="submit" class="btn btn-primary"><spring:message code="lecture.upload"/></button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</sec:authorize> 