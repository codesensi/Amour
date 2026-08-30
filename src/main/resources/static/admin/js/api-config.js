/**
 * 前后端接入开关配置 —— 全站唯一需要改动的接入配置
 *
 * mock=true  ：所有数据请求走本地 api/*.json（内容为后端 Result 格式的模拟快照）
 * mock=false ：所有数据请求走真实后端接口（自动携带 token）
 */
window.APP_API = {

    /** mock 开关：true = 本地 json 模拟；false = 请求真实后端接口 */
    mock: true,

    /** 真实后端地址：同源部署留空；分离部署填完整地址（需后端 CORS 或网关代理） */
    baseUrl: '',

    /** 后端统一响应成功业务码，与后端 ResultCode.SUCCESS 保持一致 */
    successCode: 200,

    /**
     * token 请求头名称与前缀
     * 与后端 sa-token 配置对齐：token-name: Authorization、token-prefix: Bearer
     * 真实模式下请求头形如：Authorization: Bearer {token}
     */
    tokenName: 'Authorization',
    tokenPrefix: 'Bearer',

    /** 请求超时时间（毫秒） */
    timeout: 10000
};
