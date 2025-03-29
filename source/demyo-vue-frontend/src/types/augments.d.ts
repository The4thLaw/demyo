// See https://vuejs.org/guide/typescript/options-api.html#augmenting-global-properties
import { Composer } from 'vue-i18n'

declare module 'vue' {
  interface ComponentCustomOptions  {
    $t: Composer['t']
  }
}
