<template>
	<!-- Inspired from vue-letter-avatar but with a customizable color -->
	<svg
		:style="'width: 24px; height: 24px; border-radius: 100%; background: '
			+ actualColor + ' none repeat scroll 0% 0%;'"
	>
		<text
			x="50%" y="55%" text-anchor="middle" dominant-baseline="central"
			style="fill: rgba(255, 255, 255, 0.7); font-family: sans-serif; font-size: 15px;"
		>
			{{ letter }}
		</text>
	</svg>
</template>

<script>
export default {
	name: 'LetterIcon',

	props: {
		letter: {
			type: String,
			required: true
		},
		color: {
			type: String,
			required: false,
			default: null
		}
	},

	computed: {
		actualColor() {
			if (this.color) {
				return this.color
			}
			return this.$vuetify.theme.isDark
				? this.$vuetify.theme.themes.dark.primary.base
				: this.$vuetify.theme.themes.light.primary.base
		}
	}

}
</script>
