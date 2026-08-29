/**
 * 关于页页面模块（由 pjax 页面调度加载）：对话机器人。
 * 原为 BotUI 0.3.9 驱动，现以约百行原生 JS 等价复刻；
 * 剧本数据与渲染分离——剧本可由后台接口（PORTAL_API.aboutChat）配置，
 * 未实现时 portalRequest 自动降级为 PORTAL_MOCK.aboutChat 内置默认剧本。
 */
import { portalRequest, PORTAL_MOCK } from '../portal.js';

export function init() {
  const box = document.getElementById('chat-box');
  if (!box) return;

  const sleep = function (ms) {
    return new Promise(function (resolve) { setTimeout(resolve, ms); });
  };
  const scrollToBottom = function () {
    box.scrollTop = box.scrollHeight;
  };

  // 追加一条气泡消息；content 支持 "![alt](url)" 图片语法（剧本中仅"告辞"一处）
  async function botMessage(content, delay) {
    const typing = document.createElement('div');
    typing.className = 'chat-typing';
    typing.innerHTML = '<span class="dot"></span><span class="dot"></span><span class="dot"></span>';
    box.appendChild(typing);
    scrollToBottom();
    await sleep(delay);
    typing.remove();

    const message = document.createElement('div');
    message.className = 'chat-msg';
    const image = content.trim().match(/^!\[(.*)\]\((.+)\)$/);
    if (image) {
      const img = document.createElement('img');
      img.className = 'chat-msg-image';
      img.src = image[2];
      img.alt = image[1];
      message.appendChild(img);
    } else {
      message.textContent = content;
    }
    box.appendChild(message);
    scrollToBottom();
  }

  // 渲染分支按钮组：访客点选后按钮组移除，所选"提问"以右侧气泡显示，Promise 返回所选项
  function botButtons(delay, actions) {
    return new Promise(function (resolve) {
      setTimeout(function () {
        const wrap = document.createElement('div');
        wrap.className = 'chat-actions';
        actions.forEach(function (action) {
          const button = document.createElement('button');
          button.type = 'button';
          button.className = 'chat-btn';
          button.textContent = action.text;
          button.addEventListener('click', function () {
            wrap.remove();
            // 复刻 botui 行为：所选选项作为右侧（human）气泡留在对话流中
            const chosen = document.createElement('div');
            chosen.className = 'chat-msg human';
            chosen.textContent = action.text;
            box.appendChild(chosen);
            scrollToBottom();
            resolve({ value: action.value, next: action.next });
          });
          wrap.appendChild(button);
        });
        box.appendChild(wrap);
        scrollToBottom();
      }, delay);
    });
  }

  // 渲染剧本节点序列：bot 消息或分支按钮，点选后递归执行所选分支
  async function play(nodes) {
    for (let i = 0; i < nodes.length; i++) {
      const node = nodes[i];
      if (node.type === 'buttons') {
        const chosen = await botButtons(node.delay || 0, node.options || []);
        if (chosen.next && chosen.next.length) {
          await play(chosen.next);
        }
      } else {
        await botMessage(node.content || '', node.delay || 0);
      }
    }
  }

  // 剧本获取：后台可配置（PORTAL_API.aboutChat，GET /love/chat）；
  // 接口未实现或未配置时 portalRequest 降级为 PORTAL_MOCK.aboutChat 内置默认剧本
  function startChat() {
    portalRequest('aboutChat').then(function (script) {
      if (!Array.isArray(script) || script.length === 0) {
        script = PORTAL_MOCK.aboutChat;
      }
      return play(script);
    });
  }

  startChat();
}
