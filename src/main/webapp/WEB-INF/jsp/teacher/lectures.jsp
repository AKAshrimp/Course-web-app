<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../layout/header.jsp">
    <jsp:param name="title" value="课程管理" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header bg-info text-white d-flex justify-content-between align-items-center">
                    <h3><i class="bi bi-book"></i> 课程管理</h3>
                    <a href="/teacher/lectures/add" class="btn btn-light">
                        <i class="bi bi-plus-circle"></i> 添加课程
                    </a>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>标题</th>
                                    <th>描述</th>
                                    <th>创建时间</th>
                                    <th>资料数量</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${lectures}" var="lecture">
                                    <tr>
                                        <td>${lecture.id}</td>
                                        <td>${lecture.title}</td>
                                        <td>${lecture.description}</td>
                                        <td>${lecture.createdAt}</td>
                                        <td>
                                            <span class="badge bg-secondary">${lecture.materials.size()}</span>
                                        </td>
                                        <td>
                                            <div class="btn-group" role="group">
                                                <a href="/lecture/${lecture.id}" class="btn btn-sm btn-primary">
                                                    <i class="bi bi-eye"></i> 查看
                                                </a>
                                                <a href="/teacher/lectures/edit/${lecture.id}" class="btn btn-sm btn-warning">
                                                    <i class="bi bi-pencil"></i> 编辑
                                                </a>
                                                <form action="/teacher/lectures/delete/${lecture.id}" method="post" class="d-inline" 
                                                      onsubmit="return confirm('确定要删除此课程吗？此操作不可撤销。');">
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