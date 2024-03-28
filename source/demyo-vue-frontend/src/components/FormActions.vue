<template>
	<div class="c-FormActions">
		<template v-if="!hasDefaultSlot">
			<v-btn color="secondary" class="c-FormActions__submit" type="submit" @click.prevent="$emit('save')">
				{{ $t('button.save') }}
			</v-btn>
			<v-btn
				v-if="showReset" class="c-FormActions__reset" variant="text" color="primary"
				@click="$emit('reset')"
			>
				{{ $t('button.reset') }}
			</v-btn>
			<v-btn v-if="showBack" variant="text" color="primary" @click="$router.go(-1)">
				{{ $t('button.back') }}
			</v-btn>
		</template>
		<template v-else>
			<slot />
		</template>
	</div>
</template>

<script>
export default {
	name: 'FormActions',

	props: {
		showReset: {
			type: Boolean,
			default: true
		},

		showBack: {
			type: Boolean,
			default: true
		}
	},

	computed: {
		hasDefaultSlot() {
			return !!this.$slots.default
		}
	}
}
</script>

<style lang="scss">
.c-FormActions {
	text-align: center;

	& button.v-btn {
		margin-right: 16px;
	}
}
</style>
S
