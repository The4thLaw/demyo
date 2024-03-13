<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
	<%-- The lang attribute will be adjusted by the frontend --%>

	<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width,initial-scale=1">

	<title>Demyo</title>

	<meta name="application-name" content="Demyo">
	<meta name="mobile-web-app-capable" content="yes">
	<%-- The manifest link will be injected by the Vue app -->
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-title" content="Demyo">
	<meta name="msapplication-starturl" content="${pageContext.request.contextPath}/">

	<%-- In case someone exposes it publicly by mistake --%>
	<meta name="robots" content="noindex,nofollow">
	<%-- Don't send info to linked sites, this is supposed to be a private application --%>
	<meta name="referrer" content="never">

	<link rel="copyright" href="${pageContext.request.contextPath}/about">

	<c:set var="appleIconSizes" value="${[76, 120, 152, 180]}" scope="application" />
	<c:forEach items="${appleIconSizes}" var="size">
	    <link rel="apple-touch-icon-precomposed" sizes="${size}x${size}" href="${pageContext.request.contextPath}/icons/demyo-${size}-whitebg.png">
	</c:forEach>

	<c:set var="faviconSizes" value="${[16, 24, 32, 48, 64, 144, 192, 196, 270, 558]}" scope="application" />
	<c:forEach items="${faviconSizes}" var="size">
		<link rel="icon" sizes="${size}x${size}" href="${pageContext.request.contextPath}/icons/demyo-${size}.png" >
	</c:forEach>

	<meta name="msapplication-TileColor" content="#FFFFFF">
	<meta name="msapplication-TileImage" content="${pageContext.request.contextPath}/icons/demyo-144.png">

	<%-- Note: the nonce is required on external scripts too on Firefox. Chromium Edge doesn't seem to care. --%>
	<script type="module" crossorigin src="${pageContext.request.contextPath}/assets/${indexJsFilename}" nonce="${cspScriptNonce}"></script>
	<c:forEach items="${otherJsFilenames}" var="asset">
		<link rel="modulepreload" crossorigin href="${pageContext.request.contextPath}/assets/${asset}">
	</c:forEach>
	<c:forEach items="${cssFilenames}" var="asset">
		<link rel="stylesheet" crossorigin href="${pageContext.request.contextPath}/assets/${asset}">
	</c:forEach>
</head>
<body
	data-contextRoot="${pageContext.request.contextPath}/"
	data-apiRoot="${pageContext.request.contextPath}/api/"
	data-version="${appVersion}"
	data-codename="${appCodename}"
	data-cspStyleNonce="${cspStyleNonce}"
	data-lol="${requestScope.cspStyleNonce}"
>
    <noscript>
        <strong>We're sorry but the Vue frontend to Demyo doesn't work properly without JavaScript enabled. Please enable it to continue.</strong>
    </noscript>
    <div id=app></div>
</body>
</html>
