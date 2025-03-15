<template>
	<!-- Inspired from vue-letter-avatar but with a customizable color -->
	<svg
		class="c-LetterIcon"
		:style="'width: 24px; height: 24px; border-radius: 100%; background: '
			+ actualColor + ' none repeat scroll 0% 0%;'"
	>
		<text
			x="50%" y="55%" text-anchor="middle" dominant-baseline="central"
			style="fill: rgba(255 255 255 / 70%); font-family: sans-serif; font-size: 15px;"
		>
			{{ letter }}
		</text>
	</svg>
</template>

<script setup lang="ts">
import { useTheme } from 'vuetify'

const props = withDefaults(defineProps<{
	letter: string,
	color?: string
}>(), {
	color: undefined
})

const { current } = useTheme()

const actualColor = computed(() => props.color ?? current.value.colors.primary)
</script>

<style type="css">
.v-list-item__prepend > .c-LetterIcon ~ .v-list-item__spacer {
	/* Same as other Vuetify rules */
	width: 32px;
}
</style>
