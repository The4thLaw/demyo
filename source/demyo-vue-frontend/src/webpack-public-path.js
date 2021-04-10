import { contextRoot } from '@/myenv'

/* Vue has a publicPath setting :
 - https://cli.vuejs.org/config/#publicpath
 - https://stackoverflow.com/a/65924415/109813

However:
 - The documentation states that it has issues when working with history mode for the router
 - It fails to properly configure webpack if we access the app from a deep url (like /albums/new),
   because paths will be relative to /albums, so Webpack will attempt to load resources from /albums/js/

So we need to set this dynamically
*/

// eslint-disable-next-line camelcase, no-undef
__webpack_public_path__ = contextRoot
