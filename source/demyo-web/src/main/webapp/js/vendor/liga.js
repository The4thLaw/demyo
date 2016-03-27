/* A polyfill for browsers that don't support ligatures. */
/* The script tag referring to this file must be placed before the ending body tag. */

/* To provide support for elements dynamically added, this script adds
   method 'icomoonLiga' to the window object. You can pass element references to this method.
*/
(function () {
    'use strict';
    function supportsProperty(p) {
        var prefixes = ['Webkit', 'Moz', 'O', 'ms'],
            i,
            div = document.createElement('div'),
            ret = p in div.style;
        if (!ret) {
            p = p.charAt(0).toUpperCase() + p.substr(1);
            for (i = 0; i < prefixes.length; i += 1) {
                ret = prefixes[i] + p in div.style;
                if (ret) {
                    break;
                }
            }
        }
        return ret;
    }
    var icons;
    if (!supportsProperty('fontFeatureSettings')) {
        icons = {
            'open_book': '&#xe0e0;',
            'picture_in_picture': '&#xe911;',
            'location': '&#xe052;',
            'add': '&#xe069;',
            'plane': '&#xe091;',
            'edit': '&#xe0ee;',
            'download': '&#xe0ff;',
            'upload': '&#xe100;',
            'arrow_down': '&#xe10f;',
            'arrow_left': '&#xe110;',
            'arrow_right': '&#xe111;',
            'arrow_up': '&#xe112;',
            'broken': '&#xe142;',
            'camera': '&#xe145;',
            'person_location': '&#xe1f2;',
            'menu': '&#xe20e;',
            'ellipsis_h': '&#xe20f;',
            'ellipsis_v': '&#xe210;',
            'person': '&#xe253;',
            'star': '&#xe260;',
            'star_outline': '&#xe262;',
            'delete': '&#xe287;',
            'heart': '&#xe291;',
            'heart_outline': '&#xe292;',
            'home': '&#xe29e;',
            'info': '&#xe2a3;',
            'list': '&#xe2aa;',
            'search': '&#xe2ca;',
            'settings': '&#xe2cb;',
            'shopping_cart': '&#xe2df;',
            'store': '&#xe2e3;',
            'thumbs': '&#xe302;',
            'help': '&#xe30f;',
            'tag': '&#xf02b;',
            'image': '&#xf03e;',
            'plus': '&#xf067;',
            'gift': '&#xf06b;',
            'eye': '&#xf06e;',
            'eye_slashed': '&#xf070;',
            'arrows_v': '&#xf07d;',
            'arrows_h': '&#xf07e;',
            'speech_bubble': '&#xf0e5;',
            'unlink': '&#xf127;',
            'box': '&#xf187;',
            'building': '&#xf1ad;',
            'image_file': '&#xf1c5;',
            'loading_circle': '&#xf1ce;',
            'search_advanced': '&#xf1e5;',
            'paint_brush': '&#xf1fc;',
            'pie_chart': '&#xf200;',
            'sticky_note': '&#xf24a;',
            'closed_book': '&#xe800;',
            'books': '&#xe801;',
          '0': 0
        };
        delete icons['0'];
        window.icomoonLiga = function (els) {
            var classes,
                el,
                i,
                innerHTML,
                key;
            els = els || document.getElementsByTagName('*');
            if (!els.length) {
                els = [els];
            }
            for (i = 0; ; i += 1) {
                el = els[i];
                if (!el) {
                    break;
                }
                classes = el.className;
                if (/dico/.test(classes)) {
                    innerHTML = el.innerHTML;
                    if (innerHTML && innerHTML.length > 1) {
                        for (key in icons) {
                            if (icons.hasOwnProperty(key)) {
                                innerHTML = innerHTML.replace(new RegExp(key, 'g'), icons[key]);
                            }
                        }
                        el.innerHTML = innerHTML;
                    }
                }
            }
        };
        window.icomoonLiga();
    }
}());
