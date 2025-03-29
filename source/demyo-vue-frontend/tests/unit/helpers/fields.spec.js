/* eslint-disable @typescript-eslint/naming-convention */
import { mergeModels } from '@/helpers/fields'
import { expect, test } from 'vitest'

test('Merges arrays of models', () => {
	const albums = {
		1: {
			tags: [
				{
					id: 10,
					name: 'B'
				},
				{
					id: 11,
					name: 'C'
				}
			]
		},
		2: {
			tags: [
				{
					id: 22,
					name: 'A'
				},
				{
					id: 10,
					name: 'B'
				}
			]
		}
	}

	const merged = mergeModels(albums, 'tags', 'name')
	expect(merged.length).toBe(3)
	expect(merged[0].id).toBe(22)
	expect(merged[1].id).toBe(10)
	expect(merged[2].id).toBe(11)
})
