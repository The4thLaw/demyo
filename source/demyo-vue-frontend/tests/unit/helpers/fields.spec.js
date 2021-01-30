import { mergeModels, tipTapExtensions } from '@/helpers/fields'

describe('fields.js', () => {
	it('Merges arrays of models', () => {
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

	it('Defines tipTapExtensions', () => {
		expect(tipTapExtensions.length).toBeGreaterThan(0)
	})
})
