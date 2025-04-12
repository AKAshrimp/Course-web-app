<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="${param.title}"/> - <spring:message code="app.title"/></title>
    
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    
    <!-- Custom CSS -->
    <link href="/css/styles.css" rel="stylesheet">
    
    <style>
        body {
            font-family: 'Poppins', 'Segoe UI', sans-serif;
        }
    </style>
</head>
<body>
    <!-- 获取当前URL，用于语言切换 -->
    <c:set var="currentUrl" value="${requestScope['javax.servlet.forward.request_uri']}" />
    <c:set var="queryString" value="${requestScope['javax.servlet.forward.query_string']}" />
    <c:set var="urlWithoutLang" value="${currentUrl}" />
    <c:if test="${not empty queryString}">
        <c:set var="queryParams" value="${fn:split(queryString, '&')}" />
        <c:set var="newQueryString" value="" />
        <c:forEach var="param" items="${queryParams}">
            <c:if test="${not fn:startsWith(param, 'lang=')}">
                <c:set var="newQueryString" value="${newQueryString}${empty newQueryString ? '' : '&'}${param}" />
            </c:if>
        </c:forEach>
        <c:if test="${not empty newQueryString}">
            <c:set var="urlWithoutLang" value="${urlWithoutLang}?${newQueryString}" />
        </c:if>
    </c:if>
    
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand d-flex align-items-center" href="/">
                <i class="bi bi-book-half me-2"></i> <spring:message code="app.title"/>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/"><i class="bi bi-house-door me-1"></i> <spring:message code="nav.home"/></a>
                    </li>
                    <sec:authorize access="hasRole('TEACHER')">
                        <li class="nav-item">
                            <a class="nav-link" href="/admin/dashboard">
                                <span class="badge me-1"><spring:message code="teacher.badge"/></span>
                                <i class="bi bi-speedometer2 me-1"></i> <spring:message code="nav.admin"/>
                            </a>
                        </li>
                    </sec:authorize>
                    <sec:authorize access="isAuthenticated()">
                        <li class="nav-item">
                            <a class="nav-link" href="/user/votes">
                                <i class="bi bi-check2-square me-1"></i>
                                <spring:message code="nav.myVotes"/>
                            </a>
                        </li>
                    </sec:authorize>
                </ul>
                
                <!-- 语言选择 -->
                <div class="nav-item dropdown me-3">
                    <a class="nav-link dropdown-toggle text-white" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-globe me-1"></i>
                        <c:choose>
                            <c:when test="${pageContext.response.locale.toString() == 'zh_TW'}">
                                <spring:message code="lang.zh_TW"/>
                            </c:when>
                            <c:otherwise>
                                <spring:message code="lang.en"/>
                            </c:otherwise>
                        </c:choose>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="${urlWithoutLang}${empty newQueryString ? '?' : '&'}lang=en"><spring:message code="lang.en"/></a></li>
                        <li><a class="dropdown-item" href="${urlWithoutLang}${empty newQueryString ? '?' : '&'}lang=zh_TW"><spring:message code="lang.zh_TW"/></a></li>
                    </ul>
                </div>
                
                <ul class="navbar-nav">
                    <sec:authorize access="!isAuthenticated()">
                        <li class="nav-item">
                            <a class="nav-link" href="/login"><i class="bi bi-box-arrow-in-right me-1"></i> <spring:message code="nav.login"/></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/register"><i class="bi bi-person-plus me-1"></i> <spring:message code="nav.register"/></a>
                        </li>
                    </sec:authorize>
                    <sec:authorize access="isAuthenticated()">
                        <li class="nav-item">
                            <a class="nav-link" href="/profile"><i class="bi bi-person-circle me-1"></i> <spring:message code="nav.profile"/></a>
                        </li>
                        <li class="nav-item">
                            <form action="/logout" method="post">
                                <button type="submit" class="btn btn-link nav-link"><i class="bi bi-box-arrow-right me-1"></i> <spring:message code="nav.logout"/></button>
                            </form>
                        </li>
                    </sec:authorize>
                </ul>
            </div>
        </div>
    </nav>
    
    <!-- 教师欢迎提示 -->
    <sec:authorize access="hasRole('TEACHER')">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <div class="container">
                <i class="bi bi-info-circle-fill me-2"></i> <spring:message code="teacher.welcome"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </div>
    </sec:authorize>
    
    <!-- 主内容 -->
    <div class="container my-4">
        <jsp:include page="/WEB-INF/jsp/${param.content}" />
    </div>
    
    <!-- 页脚 -->
    <footer class="py-3 mt-auto" style="background: linear-gradient(90deg, var(--primary-color), var(--primary-dark)); position: fixed; bottom: 0; width: 100%;">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-12 text-center">
                    <p class="mb-0 text-white">This site provides course materials and online polling features. &copy; 2025 Course Website</p>
                </div>
            </div>
        </div>
    </footer>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JS -->
    <script src="/js/custom.js"></script>
</body>
</html> 