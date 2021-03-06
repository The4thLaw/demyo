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
	
	<link href="${pageContext.request.contextPath}/css/${manageCssFilename}" rel="prefetch">
	<link href="${pageContext.request.contextPath}/css/${vendorFilepondCssFilename}" rel="prefetch">
	<link href="${pageContext.request.contextPath}/js/${manageJsFilename}" rel="prefetch">
	<link href="${pageContext.request.contextPath}/js/${vendorFilepondJsFilename}" rel="prefetch">
    <link href="${pageContext.request.contextPath}/css/${appCssFilename}" rel="preload" as="style">
    <link href="${pageContext.request.contextPath}/css/${vendorCssFilename}" rel="preload" as="style">
    <link href="${pageContext.request.contextPath}/js/${appJsFilename}" rel="modulepreload" as="script">
    <link href="${pageContext.request.contextPath}/js/${vendorJsFilename}" rel="modulepreload" as="script">
    <link href="${pageContext.request.contextPath}/css/${appCssFilename}" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/${vendorCssFilename}" rel="stylesheet">
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
    <%-- Note: the nonce is required on external scripts too on Firefox. Chromium Edge doesn't seem to care. --%>
    <script type="module" src="${pageContext.request.contextPath}/js/${vendorJsFilename}" nonce="${cspScriptNonce}"></script>
    <script type="module" src="${pageContext.request.contextPath}/js/${appJsFilename}" nonce="${cspScriptNonce}"></script>
    <script nonce="${cspScriptNonce}">
    !function(){var e=document,t=e.createElement("script");if(!("noModule"in t)&&"onbeforeload"in t){var n=!1;e.addEventListener("beforeload",function(e){if(e.target===t)n=!0;else if(!e.target.hasAttribute("nomodule")||!n)return;e.preventDefault()},!0),t.type="module",t.src=".",e.head.appendChild(t),t.remove()}}();
    </script>
    <script src="${pageContext.request.contextPath}/js/${vendorLegacyJsFilename}" nomodule nonce="${cspScriptNonce}"></script>
    <script src="${pageContext.request.contextPath}/js/${appLegacyJsFilename}" nomodule nonce="${cspScriptNonce}"></script>
</body>
</html>
