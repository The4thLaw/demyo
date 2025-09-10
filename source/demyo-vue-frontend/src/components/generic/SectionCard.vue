<template>
	<v-card :loading="loading ? 'primary' : false" class="c-SectionCard mb-4">
		<div v-if="!loading && !hasImage" class="c-SectionCard__container">
			<h1 v-if="title && !loading" class="text-h4">
				{{ title }}
			</h1>
			<h2 v-if="subtitle && !loading" class="text-subtitle-1 text-primary mb-4">
				{{ subtitle }}
			</h2>
			<slot />
		</div>
		<div v-if="!loading && hasImage" class="c-SectionCard__container c-SectionCard__container--image">
			<div class="c-SectionCard__image">
				<!--
				What we want for this image is:
					- On large screens, be side to side with the text
					- On medium screens, be a bit smaller
					- On small screens, be above the text and use the small version
						(so that the height is reduced as well)
					- Be nice on HiDPI and LoDPI screens
				-->
				<img
					v-fullscreen-image="{
						imageUrl: baseImageUrl,
						withDownload: false,
						maxHeight: '100vh'
					}"
					:src="`${baseImageUrl}?w=200`"
					:srcset="`
						${baseImageUrl}?w=200 200w,
						${baseImageUrl}?w=400 400w,
						${baseImageUrl}?w=700 700w`"
					sizes="(max-width: 700px) 200px, 350px"
					:alt="image.identifyingName"
				>
			</div>
			<div class="c-SectionCard__content">
				<h1 v-if="title && !loading" class="text-h4">
					{{ title }}
				</h1>
				<slot />
			</div>
		</div>
	</v-card>
</template>

<script setup lang="ts">
/**
 * A card used to section content.
 * Sports standard padding and loading style.
 */

import { getBaseImageUrl } from '@/helpers/images'

const props = withDefaults(defineProps<{
	loading?: boolean,
	title?: string,
	subtitle?: string,
	image?: Image
}>(), {
	loading: false,
	title: undefined,
	subtitle: undefined,
	image: undefined
})

const hasImage = computed(() => props.image?.id)
const baseImageUrl = computed(() => getBaseImageUrl(props.image))
</script>

<style lang="scss">
$xs-breakpoint: 600px; // From Vuetify
$padding-regular: 24px;
$padding-small: 16px;

.c-SectionCard.v-card {
	padding-bottom: $padding-regular;

	.text-h4 {
		margin-bottom: 16px;
	}
}

#demyo .c-SectionCard__container {
	padding: $padding-regular;
	padding-bottom: 0;

	@media (width <= $xs-breakpoint) {
		padding: $padding-small;
	}

	> :last-child {
		margin-bottom: 0;
	}
}

#demyo .c-SectionCard--tabbed {
	padding: 0;

	> .c-SectionCard__container {
		padding: 0;

		> .v-tabs .v-tab:not(.v-tab--selected) {
			// Need old rgba function for vuetify
			/* stylelint-disable-next-line color-function-notation */
			color: rgb(var(--v-theme-on-primary), 0.6);
		}

		> .v-window {
			padding: $padding-regular;

			@media (width <= $xs-breakpoint) {
				padding: $padding-small;
			}
		}
	}
}

.c-SectionCard__container--image {
	display: flex;

	@media (width <= $xs-breakpoint) {
		flex-direction: column;
	}
}

.c-SectionCard__content {
	padding-left: $padding-regular;
	flex: 1;

	@media (width <= $xs-breakpoint) {
		padding-left: 0;
	}
}

@media (width <= $xs-breakpoint) {
	.c-SectionCard--tabbed .v-tabs-items {
		padding: $padding-small;
	}

	.c-SectionCard__image {
		text-align: center;
	}
}

/**
 * The following two styles are only useful with mock data, which can be wider than advertised by the
 * src-set widths. Otherwise, the responsive images work just fine.
 */
.c-SectionCard__image img {
	max-width: 350px;

	@media (width <= 700px) {
		max-width: 200px;
	}
}
</style>
