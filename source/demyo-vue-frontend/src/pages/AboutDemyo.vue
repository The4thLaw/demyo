<template>
	<div class="v-About">
		<SectionCard :subtitle="$t('page.About.aboutDemyo')">
			<p>
				Demyo v{{ demyoVersion }} "{{ demyoCodename }}"
			</p>
			<!-- eslint-disable-next-line vue/no-v-html -->
			<p v-html="$t('page.About.aboutDemyo.description')" />
		</SectionCard>

		<SectionCard :subtitle="$t('page.About.aboutLibs')">
			<p v-text="$t('page.About.aboutLibs.description')" />
			<v-table>
				<template #default>
					<thead>
						<tr>
							<th scope="col">
								{{ $t('page.About.aboutLibs.libName') }}
							</th>
							<th scope="col">
								{{ $t('page.About.aboutLibs.libCopyright') }}
							</th>
							<th scope="col">
								{{ $t('page.About.aboutLibs.libLicense') }}
							</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="lib in libs" :key="lib.name">
							<td>
								<a :href="lib.url">{{ lib.name }}</a>
							</td>
							<td v-text="lib.copyright" />
							<!-- eslint-disable-next-line vue/no-v-html -->
							<td v-html="lib.license" />
						</tr>
					</tbody>
					<!-- link to package.json on GitHub -->
				</template>
			</v-table>

			<p v-text="$t('page.About.aboutLibs.env')" />
			<v-table>
				<template #default>
					<thead>
						<tr>
							<th scope="col">
								{{ $t('page.About.aboutLibs.libName') }}
							</th>
							<th scope="col">
								{{ $t('page.About.aboutLibs.libCopyright') }}
							</th>
							<th scope="col">
								{{ $t('page.About.aboutLibs.libLicense') }}
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<a href="https://maven.apache.org/">Apache Maven</a>
							</td>
							<td v-text="'Apache Software Foundation'" />
							<td>
								<a href="https://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>
							</td>
						</tr>
						<tr>
							<td>
								<a href="https://www.debian.org/">Debian</a>
							</td>
							<td v-text="'The Debian Project'" />
							<td />
						</tr>
						<tr>
							<td>
								<a href="https://eclipse.org/">Eclipse</a>
							</td>
							<td v-text="'Eclipse Foundation'" />
							<td>
								<a href="https://eclipse.org/org/documents/epl-v10.php">Eclipse Public License</a>
							</td>
						</tr>
						<tr>
							<td>
								<a href="https://nodejs.org/">Node.js</a>
							</td>
							<td v-text="'OpenJS Foundation'" />
							<td>
								<a href="https://www.opensource.org/licenses/mit-license.php">MIT License</a>
							</td>
						</tr>
						<tr>
							<td>
								<a href="https://www.npmjs.com/">npm</a>
							</td>
							<td v-text="'npm, Inc'" />
							<td>
								<a href="https://www.perlfoundation.org/artistic-license-20.html">
									Artistic License 2.0
								</a>
							</td>
						</tr>
						<tr>
							<td>
								<a href="http://openjdk.java.net/">OpenJDK</a>
							</td>
							<td />
							<td>
								<a href="https://www.gnu.org/licenses/gpl-2.0.html">GNU General Public License v2</a>
							</td>
						</tr>
						<tr>
							<td>
								<a href="https://code.visualstudio.com/">Visual Studio Code</a>
							</td>
							<td v-text="'Microsoft'" />
							<td>
								<a href="https://www.opensource.org/licenses/mit-license.php">MIT License</a>
							</td>
						</tr>
					</tbody>
				</template>
			</v-table>
		</SectionCard>

		<SectionCard :subtitle="$t('page.About.aboutInstall')">
			<p v-text="$t('page.About.aboutInstall.description')" />
			<v-table>
				<template #default>
					<thead>
						<tr>
							<th scope="col">
								{{ $t('page.About.aboutInstall.parameter') }}
							</th>
							<th scope="col">
								{{ $t('page.About.aboutInstall.value') }}
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>{{ $t('page.About.aboutInstall.parameter.os') }}</td>
							<td>{{ env.osName }} {{ env.osVersion }} ({{ env.osArch }})</td>
						</tr>
						<tr>
							<td>{{ $t('page.About.aboutInstall.parameter.java') }}</td>
							<td>{{ env.javaVendor }} Java ({{ env.javaVersion }})</td>
						</tr>
						<tr>
							<td>{{ $t('page.About.aboutInstall.parameter.userAgent') }}</td>
							<td>{{ userAgent }}</td>
						</tr>
					</tbody>
				</template>
			</v-table>
		</SectionCard>
	</div>
</template>

<script setup lang="ts">
import libs from '@/assets/about-libs.json'
import { demyoCodename, demyoVersion } from '@/myenv'
import aboutService from '@/services/about-service'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'

useHead({
	title: useI18n().t('title.about')
})

const { userAgent } = navigator

const env = ref({})

void (async () => {
	env.value = await aboutService.getEnvironment()
})()
</script>

<style lang="scss">
.v-About .v-data-table {
	margin-bottom: 2em;
}
</style>
