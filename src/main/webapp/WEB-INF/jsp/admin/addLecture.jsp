<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../layout/base.jsp">
    <jsp:param name="title" value="${param.title}" />
    <jsp:param name="content" value="admin/addLectureContent.jsp" />
</jsp:include> 