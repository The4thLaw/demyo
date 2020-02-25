<template>
	<div>
		<v-card v-if="loading" class="c-AlbumCard--loading">
			<v-card-title />
			<v-card-text>
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
			</v-card-text>
		</v-card>
		<v-card v-if="!loading" class="c-AlbumCard">
			Hello, album
		</v-card>
	</div>
</template>

<script>
export default {
	name: 'AlbumCard',

	props: {
		album: {
			type: null,
			required: true
		},

		loading: {
			type: Boolean,
			default: false
		}
	}
}
</script>

<style lang="less">
// TODO[dark] adapt load colors to dark theme

.c-AlbumCard__contentFaker {
	min-height: 1.75em;
	margin-top: 1em;
	margin-bottom: 1em;
}

.c-AlbumCard--loading {
	.v-card__title {
		min-height: 2.5em;
	}

	.v-card__title,
	.c-AlbumCard__contentFaker {
		background-color: #bbb;
		animation: 1.5s ease-in-out 0s infinite alternate pulsate;

		@media (prefers-reduced-motion: reduce) {
			animation: none;
		}
	}

	.c-AlbumCard__contentFaker:nth-child(1) {
		max-width: 90%;
	}

	.c-AlbumCard__contentFaker:nth-child(2) {
		max-width: 70%;
	}

	.c-AlbumCard__contentFaker:nth-child(3) {
		max-width: 80%;
	}

	.c-AlbumCard__contentFaker:nth-child(4) {
		max-width: 95%;
	}
}

@keyframes pulsate {
	/*
	 * Animating opacity is much, much more efficient than animating the background color,
	 * which requires repaints. See
	 * https://developer.mozilla.org/en-US/docs/Tools/Performance/Scenarios/Animating_CSS_properties#CSS_property_cost
	 */
	from {
		opacity: 1;
	}

	to {
		opacity: 0.5;
	}
}
</style>
