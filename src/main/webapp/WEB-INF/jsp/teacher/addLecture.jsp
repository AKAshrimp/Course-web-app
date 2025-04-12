<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../layout/header.jsp">
    <jsp:param name="title" value="添加课程" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header bg-info text-white">
                    <h3><i class="bi bi-plus-circle"></i> 添加新课程</h3>
                </div>
                <div class="card-body">
                    <form:form action="/teacher/lectures/add" method="post" modelAttribute="lecture" class="needs-validation">
                        <div class="mb-3">
                            <label for="title" class="form-label">课程标题 <span class="text-danger">*</span></label>
                            <form:input path="title" id="title" class="form-control" required="true" />
                            <form:errors path="title" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">课程描述 <span class="text-danger">*</span></label>
                            <form:textarea path="description" id="description" class="form-control" rows="5" required="true" />
                            <form:errors path="description" cssClass="text-danger" />
                        </div>
                        
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-info text-white">
                                <i class="bi bi-save"></i> 创建课程
                            </button>
                            <a href="/teacher/lectures" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> 返回
                            </a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" /> 