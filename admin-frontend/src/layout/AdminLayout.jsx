import { Link, useLocation, useNavigate } from "react-router-dom";

import { LogoutOutlined, TeamOutlined } from "@ant-design/icons";
import { Avatar, Button, Layout, Menu, Typography } from "antd";

import { useAuth } from "../auth/AuthContext";

const { Content, Header, Sider } = Layout;
const { Text, Title } = Typography;

export default function AdminLayout({ children, title, subtitle }) {
  const auth = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  function handleLogout() {
    auth.logout();
    navigate("/login");
  }

  return (
    <Layout className="admin-layout">
      <Sider breakpoint="lg" collapsedWidth="0" className="admin-sider">
        <div className="brand">
          <Title level={4}>Course Admin</Title>
          <Text>Account Management</Text>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname.startsWith("/users") ? "users" : ""]}
          items={[
            {
              key: "users",
              icon: <TeamOutlined />,
              label: <Link to="/users">Users</Link>
            }
          ]}
        />
      </Sider>

      <Layout>
        <Header className="admin-header">
          <div>
            <Title level={3}>{title}</Title>
            {subtitle && <Text type="secondary">{subtitle}</Text>}
          </div>
          <div className="header-user">
            <Avatar>{auth.username?.charAt(0).toUpperCase()}</Avatar>
            <Text>{auth.username}</Text>
            <Button icon={<LogoutOutlined />} onClick={handleLogout}>
              Logout
            </Button>
          </div>
        </Header>

        <Content className="admin-content">{children}</Content>
      </Layout>
    </Layout>
  );
}
