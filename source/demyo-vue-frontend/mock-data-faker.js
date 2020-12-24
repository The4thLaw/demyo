const faker = require('faker')
const actualData = require('./mock-data-actual.json')

faker.seed(42)

/**
 * Given a sufficient number of invocations, this function will return ratio of true and (1-ratio) of false values.
 * @param {float} ratio
 * @return {boolean}
 */
function luckRatio(ratio) {
	const number = (faker.random.number() % 10 + 1) / 10
	return number <= ratio
}

function paragraphs(maxNum) {
	let count = faker.random.number() % maxNum
	let paragraphs = ''
	do {
		paragraphs += `<p>${faker.lorem.paragraph()}</p>`
	} while (count-- >= 0)
	return paragraphs
}

// AUTHORS
const authors = []
for (let i = 0; i < 250; i++) {
	const lname = faker.name.lastName()
	const fname = faker.name.firstName()
	const author = {
		id: i,
		name: lname,
		firstName: fname,
		identifyingName: `${fname} ${lname}`
	}
	if (luckRatio(0.2)) {
		const nick = faker.name.firstName()
		author.nickname = nick
		author.identifyingName = `${fname} '${nick}' ${lname}`
	}
	if (luckRatio(0.5)) {
		author.biography = paragraphs(2)
	}
	if (luckRatio(0.5)) {
		author.website = faker.internet.url()
	}
	if (luckRatio(0.5)) {
		author.portrait = {
			id: faker.random.number() % 2 + 1,
			userFileName: 'sample image.jpg',
			description: author.identifyingName + ' - Portrait',
			identifyingName: author.identifyingName + ' - Portrait'
		}
	}
	authors.push(author)
}
authors.sort((a, b) => a.name.localeCompare(b.name))

module.exports = () => {
	return {
		authors: authors,
		translations_en: actualData.translations_en,
		translations_fr: actualData.translations_fr
	}
}
