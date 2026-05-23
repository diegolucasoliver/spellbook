# Diretrizes para Assistentes de IA

Este arquivo contém as regras de desenvolvimento que devem ser seguidas rigorosamente por qualquer assistente de IA que atue neste projeto.

## Regras de Interface (UI)

*   **Jetpack Compose Exclusivo:** Toda a interface do usuário deve ser construída exclusivamente utilizando Jetpack Compose.
*   **Sem XML:** Não é permitido o uso de arquivos de layout XML (`res/layout`).
*   **Sem AndroidView:** Não deve ser utilizado o componente `AndroidView` para integrar Views tradicionais. Tudo deve ser implementado de forma nativa no Compose.
*   **Previews Obrigatórios:** Todo componente `@Composable` deve possuir ao menos um `@Preview` correspondente para facilitar o desenvolvimento e garantir a testabilidade visual.

## Padrões de Código

*   **Linguagem de Código:** Todo o código (nomes de variáveis, funções, classes, enums, comentários técnicos) deve ser escrito em **Inglês**.
*   **Linguagem de Interface:** Todo o conteúdo visível para o usuário final (textos, dicas, nomes de magias, nomes de escolas no app) deve ser em **Português (Brasil)**.
*   **Arquitetura:** Seguir os padrões modernos de Android (MVVM, ViewModel, Flow/StateFlow).
*   **Injeção de Dependência:** Facilitar testabilidade permitindo injeção de serviços via construtor (ex: `SpellViewModel`).
*   **Testes Obrigatórios:** Para todo código novo ou alterado, verificar se é necessário criar ou atualizar testes unitários. Se sim, implementá-los imediatamente.
*   **Snapshot Testing:** Todos os componentes visuais (Composables) que possuam `@Preview` devem ter testes de snapshot correspondentes utilizando **Roborazzi**. Para toda alteração de componente visual, os testes de snapshot devem ser validados e atualizados se a mudança for intencional (utilizando o comando `./gradlew recordRoborazziDebug`).

## Firebase / Firestore

*   **Mapeamento de Dados:** Utilizar enums com `@PropertyName` para mapear campos do banco de dados (que estão em português) para o código (em inglês).
*   **Segurança:** Sempre garantir que as operações dependam de autenticação (ex: Firebase Auth Anonymous).
