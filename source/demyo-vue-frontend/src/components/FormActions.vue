<template>
	<div class="c-FormActions">
		<template v-if="!hasDefaultSlot">
			<v-btn color="accent" type="submit" @click.prevent="$emit('save')">
				{{ $t('button.save') }}
			</v-btn>
			<v-btn v-if="showReset" text color="primary" @click="$emit('reset')">
				{{ $t('button.reset') }}
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
		}
	},

	computed: {
		hasDefaultSlot() {
			return !!this.$slots.default
		}
	}
}
</script>

<style lang="less">
.c-FormActions {
	text-align: center;

	& button.v-btn {
		margin-right: 16px;
	}
}
</style>
S
