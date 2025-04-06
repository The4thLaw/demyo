<template>
	<v-dialog
		v-model="dialog" :persistent="requireSelection" max-width="400px"
		@click:outside="emit('cancel')"
	>
		<v-card>
			<v-card-title>
				{{ $t('page.Reader.select.title') }}
			</v-card-title>
			<v-card-text>
				<p>
					{{ $t('page.Reader.select.explanation.base') }}
				</p>
				<v-list>
					<v-list-item
						v-for="reader in readers" :key="reader.id"
						:title="reader.identifyingName" @click="select(reader)"
					>
						<template #prepend>
							<LetterIcon :letter="reader.identifyingName.charAt(0)" :color="reader.colour" />
						</template>
					</v-list-item>
				</v-list>
			</v-card-text>

			<v-card-actions v-if="!requireSelection">
				<v-spacer />

				<v-btn color="primary" variant="text" @click="dialog = false; emit('cancel')">
					{{ $t('quickTasks.confirm.cancel.label') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>

<script setup lang="ts">
import readerService from '@/services/reader-service'

withDefaults(defineProps<{
	requireSelection?: boolean
}>(), {
	requireSelection: false
})

const emit = defineEmits(['select', 'cancel'])

const dialog = ref(true)
const readers = ref([] as Reader[])

async function fetchData(): Promise<void> {
	readers.value = await readerService.findForIndex()
}

async function select(reader: Reader): Promise<void> {
	await readerService.setCurrentReader(reader)
	emit('select')
}

void fetchData()
</script>
