<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../layout/header.jsp">
    <jsp:param name="title" value="教师仪表盘" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-12">
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h3><i class="bi bi-speedometer2"></i> 教师管理中心</h3>
                </div>
                <div class="card-body">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle"></i> 欢迎来到教师管理中心，在这里您可以管理课程、用户和投票。
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row">
        <!-- 用户管理卡片 -->
        <div class="col-md-4 mb-4">
            <div class="card h-100">
                <div class="card-header bg-success text-white">
                    <h4><i class="bi bi-people"></i> 用户管理</h4>
                </div>
                <div class="card-body">
                    <p>管理系统用户，添加新用户或更新现有用户信息。</p>
                    <p><strong>当前用户数:</strong> ${users.size()}</p>
                </div>
                <div class="card-footer">
                    <a href="/teacher/users" class="btn btn-success w-100">
                        <i class="bi bi-people"></i> 管理用户
                    </a>
                </div>
            </div>
        </div>
        
        <!-- 课程管理卡片 -->
        <div class="col-md-4 mb-4">
            <div class="card h-100">
                <div class="card-header bg-info text-white">
                    <h4><i class="bi bi-book"></i> 课程管理</h4>
                </div>
                <div class="card-body">
                    <p>创建和管理课程，上传课程资料。</p>
                    <p><strong>当前课程数:</strong> ${lectures.size()}</p>
                </div>
                <div class="card-footer">
                    <a href="/teacher/lectures" class="btn btn-info w-100 text-white">
                        <i class="bi bi-book"></i> 管理课程
                    </a>
                </div>
            </div>
        </div>
        
        <!-- 投票管理卡片 -->
        <div class="col-md-4 mb-4">
            <div class="card h-100">
                <div class="card-header bg-warning text-dark">
                    <h4><i class="bi bi-bar-chart"></i> 投票管理</h4>
                </div>
                <div class="card-body">
                    <p>创建和管理调查投票，查看投票结果。</p>
                    <p><strong>当前投票数:</strong> ${polls.size()}</p>
                </div>
                <div class="card-footer">
                    <a href="/teacher/polls" class="btn btn-warning w-100">
                        <i class="bi bi-bar-chart"></i> 管理投票
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 最近活动 -->
    <div class="row mt-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header bg-secondary text-white">
                    <h4><i class="bi bi-activity"></i> 最近课程</h4>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>标题</th>
                                    <th>描述</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${lectures}" var="lecture" begin="0" end="4">
                                    <tr>
                                        <td>${lecture.title}</td>
                                        <td>${lecture.description}</td>
                                        <td>${lecture.createdAt}</td>
                                        <td>
                                            <a href="/lecture/${lecture.id}" class="btn btn-sm btn-primary">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            <a href="/teacher/lectures/edit/${lecture.id}" class="btn btn-sm btn-warning">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" /> 