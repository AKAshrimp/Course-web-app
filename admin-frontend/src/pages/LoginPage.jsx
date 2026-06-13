import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { Button, Card, Form, Input, Typography, message } from "antd";
import { LockOutlined, UserOutlined } from "@ant-design/icons";

import { useAuth } from "../auth/AuthContext";

const { Text, Title } = Typography;

export default function LoginPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  async function handleSubmit(values) {
    setLoading(true);

    try {
      await auth.login(values.username, values.password);
      navigate("/users");
    } catch (error) {
      message.error("Login failed. Please check the account and role.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-shell">
      <Card className="login-card">
        <div className="login-title">
          <Title level={2}>Course Admin</Title>
          <Text type="secondary">Sign in with a teacher account</Text>
        </div>

        <Form layout="vertical" onFinish={handleSubmit} requiredMark={false}>
          <Form.Item
            label="Username"
            name="username"
            rules={[{ required: true, message: "Please enter username" }]}
          >
            <Input prefix={<UserOutlined />} placeholder="teacher" size="large" />
          </Form.Item>

          <Form.Item
            label="Password"
            name="password"
            rules={[{ required: true, message: "Please enter password" }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="password" size="large" />
          </Form.Item>

          <Button block type="primary" htmlType="submit" size="large" loading={loading}>
            Login
          </Button>
        </Form>
      </Card>
    </main>
  );
}
