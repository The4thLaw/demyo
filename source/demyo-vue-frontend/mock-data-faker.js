let faker = require('faker')
let actualData = require('./mock-data-actual.json')

faker.seed(42)

function luckRatio(ratio) {
	let number = (faker.random.number() % 10 + 1) / 10
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
let authors = []
for (let i = 0; i < 250; i++) {
	let lname = faker.name.lastName()
	let fname = faker.name.firstName()
	let author = {
		id: i,
		name: lname,
		firstName: fname,
		identifyingName: `${fname} ${lname}`
	}
	if (luckRatio(0.2)) {
		let nick = faker.name.firstName()
		author.nickname = nick
		author.identifyingName = `${fname} '${nick}' ${lname}`
	}
	if (luckRatio(0.5)) {
		author.biography = paragraphs(2)
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
