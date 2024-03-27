<template>
	<div class="c-DerivativeStickers">
		<div class="c-DerivativeStickers__Header no-print">
			<v-btn to="/">
				<v-icon start>
					mdi-home
				</v-icon>
				{{ $t('menu.main.home') }}
			</v-btn>
			<v-btn @click="print">
				<v-icon start>
					mdi-printer
				</v-icon>
				{{ $t('page.Derivative.stickers.print') }}
			</v-btn>
			<p v-text="$t('page.Derivative.stickers.explanation')" />
		</div>
		<div v-if="!initialized" v-text="$t('core.loading')" />
		<div v-if="initialized" class="c-DerivativeStickers__List">
			<div v-for="derivative in derivatives" :key="derivative.id" class="c-DerivativeStickers__Sticker">
				<div>
					<p v-if="derivative.series" class="series" v-text="derivative.series.identifyingName" />
					<p v-if="derivative.album" class="album" v-text="derivative.album.identifyingName" />
					<p v-if="derivative.artist" class="author" v-text="derivative.artist.identifyingName" />
					<p v-if="derivative.source" class="album" v-text="derivative.source.identifyingName" />
					<p class="type_number">
						{{ derivative.type.identifyingName }}
						<template v-if="derivative.number && derivative.total">
							{{ $n(derivative.number) + ' / ' + $n(derivative.total) }}
						</template>
						<span
							v-if="derivative.authorsCopy" v-text="$t('field.Derivative.authorsCopy.view.short')"
						/>
						<span
							v-if="derivative.restrictedSale" v-text="$t('field.Derivative.restrictedSale.view.short')"
						/>
					</p>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import derivativeService from '@/services/derivative-service'

export default {
	name: 'DerivativeStickers',

	data() {
		return {
			initialized: false,
			derivatives: []
		}
	},

	head() {
		return {
			title: this.$t('title.index.derivative.stickers')
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.derivatives = await derivativeService.findForIndex(null, 'full')
			this.initialized = true
		},

		print() {
			window.print()
		}
	}
}
</script>

<style lang="less">
.c-DerivativeStickers {
	background-color: white;
	color: black;
	font-family: sans-serif;
	font-size: 12pt;

	&__Header {
		padding: 1em;

		.v-btn {
			margin: 1em;
		}
	}
}

.c-DerivativeStickers__List {
	font-size: 6pt;
}

.c-DerivativeStickers__Sticker {
	display: inline-block;
	border: 0.2mm dotted #ddd;
	padding: 1mm;
	margin: 2.5mm;
	line-height: initial;

	div {
		min-width: 3cm;
		border: 0.2mm solid black;
		padding: 1mm 3mm 1mm 3mm;
		text-align: center;
	}

	p {
		margin: 0;
		padding: 0;

		&.series {
			font-variant: small-caps;
			font-size: 7pt;
		}

		&.album span {
			margin-right: 4pt;
		}
	}
}
</style>
