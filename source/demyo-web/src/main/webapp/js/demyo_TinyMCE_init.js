/**
 * Initializes TinyMCE according to the common pattern in Demyo.
 */
function demyoTinyMCEInit(lang) {
	tinyMCE_GZ.init({
		plugins: 'nonbreaking,preview,tabfocus,visualchars,xhtmlxtras',
		themes: 'advanced',
		languages: lang,
		disk_cache: true,
		debug: false
	});
	tinyMCE.init({
		plugins : 'nonbreaking,preview,tabfocus,visualchars,xhtmlxtras',
		theme: 'advanced',
		theme_advanced_toolbar_location: 'top',
		theme_advanced_toolbar_align: 'left',
		theme_advanced_statusbar_location: 'bottom',
		theme_advanced_resizing: true,
		theme_advanced_buttons1: 'formatselect,|,bold,italic,underline,del,|,sub,sup,|,abbr,acronym,|,forecolor,backcolor',
		theme_advanced_buttons2: 'bullist,numlist,|,justifyleft,justifycenter,justifyright,justifyfull,|,outdent,indent,blockquote,|,link,unlink,|,charmap,|,visualchars,nonbreaking,|,cleanup,removeformat,code',
		theme_advanced_buttons3: '',
		theme_advanced_buttons4: '',
		theme_advanced_resizing_use_cookie : false,
		language: lang,
		debug: false,
		entity_encoding : 'raw',
		mode: 'specific_textareas',
		editor_selector: 'richtext'
	});
}