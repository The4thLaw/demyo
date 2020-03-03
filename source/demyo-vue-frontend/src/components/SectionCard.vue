<template>
	<v-card :loading="loading ? 'primary' : false" class="mb-4">
		<div class="pa-6">
			<template v-if="!loading && !image">
				<h1 v-if="title && !loading" class="display-1">
					{{ title }}
				</h1>
				<h2 v-if="subtitle && !loading" class="subtitle-1 primary--text">
					{{ subtitle }}
				</h2>
				<slot />
			</template>
			<div v-if="!loading && image" class="c-SectionCard__container">
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
						v-img="{src: baseImageUrl}"
						:src="`${baseImageUrl}?w=200`"
						:srcset="`
							${baseImageUrl}?w=200 200w,
							${baseImageUrl}?w=400 400w,
							${baseImageUrl}?w=700 700w`"
						sizes="(max-width: 700px) 200px, 350px"
					>
				</div>
				<div class="c-SectionCard__content">
					<h1 v-if="title && !loading" class="display-1">
						{{ title }}
					</h1>
					<slot />
				</div>
			</div>
		</div>
	</v-card>
</template>

<script>
import { getBaseImageUrl } from '@/helpers/images'

/**
 * A card used to section content.
 * Sports standard padding and loading style.
 */
export default {
	name: 'SectionCard',

	props: {
		loading: {
			type: Boolean,
			required: false,
			default: false
		},

		title: {
			type: String,
			required: false,
			default: undefined
		},

		subtitle: {
			type: String,
			required: false,
			default: undefined
		},

		image: {
			type: Object,
			required: false,
			default: null
		}
	},

	computed: {
		baseImageUrl() {
			return getBaseImageUrl(this.image)
		}
	}
}
</script>

<style lang="less">
.c-SectionCard__container {
	display: flex;
}

.c-SectionCard__content {
	padding-left: 24px;
}

@media (max-width: 550px) {
	.c-SectionCard__container {
		flex-direction: column;
	}

	.c-SectionCard__image {
		text-align: center;
	}

	.c-SectionCard__content {
		padding-left: 0;
	}
}

/**
 * The following two styles are only useful with mock data, which can be wider than advertised by the
 * src-set widths. Otherwise, the responsive images work just fine.
 */
.c-SectionCard__image img {
	max-width: 350px;
}

@media (max-width: 700px) {
	.c-SectionCard__image img {
		max-width: 200px;
	}
}
</style>
